package com.romerojdev.infinito.pos_ai.services;

import com.romerojdev.infinito.pos_ai.model.Product;
import com.romerojdev.infinito.pos_ai.repository.QueryProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * QueryProductService provides advanced search capabilities for products in MongoDB.
 * <p>
 * Main features:
 * <ul>
 *   <li><b>queryByTokens</b>: OR search, returns products matching any token in the input.</li>
 *   <li><b>queryByAllTokens</b>: AND search, returns products matching all tokens in the input.</li>
 *   <li><b>queryByTokensSmart</b>: Tries AND search first, falls back to OR search if no results.</li>
 *   <li><b>smartSearchGrow</b>: Progressive AND search. Removes stopwords, tries all combinations of tokens (from largest to pairs),
 *       collecting unique results. Useful for fuzzy or incomplete queries.</li>
 * </ul>
 * <p>
 * Stopwords like "de" and "o" are ignored in smartSearchGrow. The service uses MongoDB queries for efficient token matching.
 *
 * Example usage:
 * <pre>
 *   // Returns products matching all tokens
 *   queryByAllTokens("leche deslactosada 1000 mililitros");
 *   // Returns products matching any token
 *   queryByTokens("leche deslactosada 1000 mililitros");
 *   // Smart search: tries all, then any
 *   queryByTokensSmart("leche deslactosada 1000 mililitros");
 *   // Progressive search: tries all combinations
 *   smartSearchGrow("leche deslactosada de 1000 mililitros");
 * </pre>
 */
@Service
public class QueryProductService {
    @Autowired
    private QueryProductRepository queryProductRepository;

    /**
     * Returns products matching any of the tokens in the input (OR logic).
     * @param input the input string to split into tokens
     * @return list of products matching any token
     */
    public List<Product> queryByTokens(String input) {
        List<String> tokens = Arrays.stream(input.toLowerCase().split("\\s+"))
                .distinct()
                .collect(Collectors.toList());
        return queryProductRepository.findByTokensIn(tokens);
    }

    /**
     * Returns products matching all tokens in the input (AND logic).
     * @param input the input string to split into tokens
     * @return list of products matching all tokens
     */
    public List<Product> queryByAllTokens(String input) {
        List<String> tokens = Arrays.stream(input.toLowerCase().split("\\s+"))
                .distinct()
                .collect(Collectors.toList());
        return queryProductRepository.findByTokensAll(tokens);
    }

    /**
     * Smart search: tries AND logic first, falls back to OR logic if no results.
     * @param input the input string to split into tokens
     * @return list of products matching all tokens, or any token if no all-tokens match
     */
    public List<Product> queryByTokensSmart(String input) {
        List<Product> allMatch = queryByAllTokens(input);
        if (!allMatch.isEmpty()) {
            return allMatch;
        }
        return queryByTokens(input);
    }

    /**
     * Progressive AND search. Removes stopwords, tries all combinations of tokens (from largest to pairs),
     * collecting unique results. Useful for fuzzy or incomplete queries.
     * @param input the input string to split into tokens
     * @return list of unique products matching the most relevant token combinations
     */
    public List<Product> smartSearchGrow(String input) {
        // Remove stopwords (e.g., "de")
        List<String> stopwords = List.of("de", "o");
        List<String> tokens = Arrays.stream(input.toLowerCase().split("\\s+")).filter(t -> !stopwords.contains(t)).distinct().collect(Collectors.toList());
        Set<Product> resultSet = new LinkedHashSet<>();
        int n = tokens.size();
        // Try all combinations from n down to 2
        for (int k = n; k >= 2; k--) {
            Set<Set<String>> combinations = new HashSet<>();
            combine(tokens, k, 0, new LinkedHashSet<>(), combinations);
            for (Set<String> combo : combinations) {
                String query = String.join(" ", combo);
                resultSet.addAll(queryByAllTokens(query));
            }
            if (!resultSet.isEmpty()) break; // Optionally stop at first found
        }
        return new ArrayList<>(resultSet);
    }

    /**
     * Helper to generate all combinations of k elements from the tokens list.
     * @param tokens the list of tokens
     * @param k the size of each combination
     * @param start the starting index
     * @param current the current combination being built
     * @param result the set to collect all combinations
     */
    private void combine(List<String> tokens, int k, int start, Set<String> current, Set<Set<String>> result) {
        if (current.size() == k) {
            result.add(new LinkedHashSet<>(current));
            return;
        }
        for (int i = start; i < tokens.size(); i++) {
            current.add(tokens.get(i));
            combine(tokens, k, i + 1, current, result);
            current.remove(tokens.get(i));
        }
    }
}
