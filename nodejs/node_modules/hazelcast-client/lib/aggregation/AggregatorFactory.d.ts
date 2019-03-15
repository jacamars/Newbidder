import { IdentifiedDataSerializable, IdentifiedDataSerializableFactory } from '../serialization/Serializable';
export declare class AggregatorFactory implements IdentifiedDataSerializableFactory {
    static readonly FACTORY_ID: number;
    static readonly BIG_DECIMAL_AVG: number;
    static readonly BIG_DECIMAL_SUM: number;
    static readonly BIG_INT_AVG: number;
    static readonly BIG_INT_SUM: number;
    static readonly COUNT: number;
    static readonly DISTINCT: number;
    static readonly DOUBLE_AVG: number;
    static readonly DOUBLE_SUM: number;
    static readonly FIXED_SUM: number;
    static readonly FLOATING_POINT_SUM: number;
    static readonly INT_AVG: number;
    static readonly INT_SUM: number;
    static readonly LONG_AVG: number;
    static readonly LONG_SUM: number;
    static readonly MAX: number;
    static readonly MIN: number;
    static readonly NUMBER_AVG: number;
    static readonly MAX_BY: number;
    static readonly MIN_BY: number;
    private idToConstructor;
    constructor();
    create(type: number): IdentifiedDataSerializable;
}
