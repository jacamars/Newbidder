import { NearCache } from './NearCache';
import { SerializationService } from '../serialization/SerializationService';
import HazelcastClient from '../HazelcastClient';
export declare class NearCacheManager {
    protected readonly serializationService: SerializationService;
    private readonly caches;
    private readonly client;
    constructor(client: HazelcastClient);
    getOrCreateNearCache(name: string): NearCache;
    destroyNearCache(name: string): void;
    destroyAllNearCaches(): void;
    listAllNearCaches(): NearCache[];
}
