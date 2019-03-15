import { Predicate } from '../core/Predicate';
import { DataInput, DataOutput } from './Data';
import { IdentifiedDataSerializable, IdentifiedDataSerializableFactory } from './Serializable';
export declare const PREDICATE_FACTORY_ID = -32;
export declare abstract class AbstractPredicate implements Predicate {
    abstract readData(input: DataInput): any;
    abstract writeData(output: DataOutput): void;
    getFactoryId(): number;
    abstract getClassId(): number;
}
export declare class PredicateFactory implements IdentifiedDataSerializableFactory {
    private idToConstructorMap;
    constructor(allPredicates: any);
    create(type: number): IdentifiedDataSerializable;
}
