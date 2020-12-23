import { Data } from './serialization/Data';
export declare class DataKeyedHashMap<T> {
    size: number;
    private internalStore;
    constructor();
    clear(): void;
    delete(key: Data): boolean;
    has(key: Data): boolean;
    get(key: Data): T;
    set(key: Data, value: any): this;
    values(): T[];
    entries(): Array<[Data, T]>;
    /**
     *
     * @param key
     * @returns index of the key if it exists, -1 if either bucket or item does not exist
     */
    private findIndexInBucket(key);
    private getOrCreateBucket(key);
}
