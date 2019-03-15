/// <reference types="long" />
import * as Long from 'long';
import { DataInput, DataOutput } from '../serialization/Data';
import { IdentifiedDataSerializable } from '../serialization/Serializable';
export interface Aggregator<R> {
}
export declare abstract class AbstractAggregator<R> implements IdentifiedDataSerializable, Aggregator<R> {
    protected attributePath: string;
    constructor(attributePath?: string);
    getFactoryId(): number;
    abstract getClassId(): number;
    abstract readData(input: DataInput): any;
    abstract writeData(output: DataOutput): void;
}
export declare class CountAggregator extends AbstractAggregator<Long> {
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
    getClassId(): number;
}
export declare class DoubleAverageAggregator extends AbstractAggregator<number> {
    getClassId(): number;
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
}
export declare class DoubleSumAggregator extends AbstractAggregator<number> {
    getClassId(): number;
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
}
export declare class NumberAverageAggregator extends AbstractAggregator<number> {
    getClassId(): number;
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
}
export declare class FixedPointSumAggregator extends AbstractAggregator<Long> {
    getClassId(): number;
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
}
export declare class FloatingPointSumAggregator extends AbstractAggregator<number> {
    getClassId(): number;
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
}
export declare class MaxAggregator<R> extends AbstractAggregator<R> {
    getClassId(): number;
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
}
export declare class MinAggregator<R> extends AbstractAggregator<R> {
    getClassId(): number;
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
}
export declare class IntegerAverageAggregator extends AbstractAggregator<number> {
    getClassId(): number;
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
}
export declare class IntegerSumAggregator extends AbstractAggregator<Long> {
    getClassId(): number;
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
}
export declare class LongAverageAggregator extends AbstractAggregator<number> {
    getClassId(): number;
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
}
export declare class LongSumAggregator extends AbstractAggregator<Long> {
    getClassId(): number;
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
}
