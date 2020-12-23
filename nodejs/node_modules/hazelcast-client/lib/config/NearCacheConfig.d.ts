import { EvictionPolicy } from './EvictionPolicy';
import { InMemoryFormat } from './InMemoryFormat';
export declare class NearCacheConfig {
    name: string;
    /**
     * 'true' to invalidate entries when they are changed in cluster,
     * 'false' to invalidate entries only when they are accessed.
     */
    invalidateOnChange: boolean;
    /**
     * Max number of seconds that an entry can stay in the cache until it is acceessed
     */
    maxIdleSeconds: number;
    inMemoryFormat: InMemoryFormat;
    /**
     * Maximum number of seconds that an entry can stay in cache.
     */
    timeToLiveSeconds: number;
    evictionPolicy: EvictionPolicy;
    evictionMaxSize: number;
    evictionSamplingCount: number;
    evictionSamplingPoolSize: number;
    toString(): string;
    clone(): NearCacheConfig;
}
