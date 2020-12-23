export declare class ConfigPatternMatcher {
    /**
     *
     * @param configPatterns
     * @param itemName
     * @throws
     * @returns `null` if there is no matching pattern
     *          the best matching pattern otherwis
     */
    matches(configPatterns: string[], itemName: string): string;
    getMatchingPoint(pattern: string, itemName: string): number;
}
