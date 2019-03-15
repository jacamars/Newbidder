export declare class FlakeIdGeneratorConfig {
    /**
     * Sets the name for this config.
     */
    name: string;
    /**
     * Sets how many IDs are pre-fetched on the background when a new flake id is requested
     * from servers. Default is 100.
     *
     * prefetch count should be in the range 1..100,000.
     */
    prefetchCount: number;
    /**
     * Sets for how long the pre-fetched IDs can be used. If this time elapses, a new batch of IDs will be
     * fetched. Time unit is milliseconds, default is 600,000 (10 minutes).
     * <p>
     * The IDs contain timestamp component, which ensures rough global ordering of IDs. If an ID
     * is assigned to an object that was created much later, it will be much out of order. If you don't care
     * about ordering, set this value to 0.
     *
     * Set to the desired ID validity or 0 for unlimited.
     */
    prefetchValidityMillis: number;
    toString(): string;
    clone(): FlakeIdGeneratorConfig;
}
