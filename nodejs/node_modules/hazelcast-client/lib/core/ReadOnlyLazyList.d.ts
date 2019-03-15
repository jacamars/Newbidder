import { SerializationService } from '../serialization/SerializationService';
export declare class ReadOnlyLazyList<T> implements Iterable<T> {
    private internalArray;
    private serializationService;
    constructor(array: any[], serializationService: SerializationService);
    get(index: number): T;
    size(): number;
    values(): Iterator<T>;
    slice(start: number, end?: number): ReadOnlyLazyList<T>;
    toArray(): T[];
    [Symbol.iterator](): Iterator<T>;
}
