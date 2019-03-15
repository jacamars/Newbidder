import { Comparator } from '../core/Comparator';
import { IterationType, Predicate } from '../core/Predicate';
import { DataInput, DataOutput } from './Data';
import { AbstractPredicate } from './PredicateFactory';
export declare class SqlPredicate extends AbstractPredicate {
    private sql;
    constructor(sql: string);
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
    getClassId(): number;
}
export declare class AndPredicate extends AbstractPredicate {
    private predicates;
    constructor(...predicates: Predicate[]);
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
    getClassId(): number;
}
export declare class BetweenPredicate extends AbstractPredicate {
    private field;
    private from;
    private to;
    constructor(field: string, from: any, to: any);
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
    getClassId(): number;
}
export declare class EqualPredicate extends AbstractPredicate {
    private field;
    private value;
    constructor(field: string, value: any);
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
    getClassId(): number;
}
export declare class GreaterLessPredicate extends AbstractPredicate {
    private field;
    private value;
    private equal;
    private less;
    constructor(field: string, value: any, equal: boolean, less: boolean);
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
    getClassId(): number;
}
export declare class LikePredicate extends AbstractPredicate {
    private field;
    private expr;
    constructor(field: string, expr: string);
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
    getClassId(): number;
}
export declare class ILikePredicate extends LikePredicate {
    getClassId(): number;
}
export declare class InPredicate extends AbstractPredicate {
    private field;
    private values;
    constructor(field: string, ...values: any[]);
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
    getClassId(): number;
}
export declare class InstanceOfPredicate extends AbstractPredicate {
    private className;
    constructor(className: string);
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
    getClassId(): number;
}
export declare class NotEqualPredicate extends EqualPredicate {
    getClassId(): number;
}
export declare class NotPredicate extends AbstractPredicate {
    private pred;
    constructor(pred: Predicate);
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
    getClassId(): number;
}
export declare class OrPredicate extends AbstractPredicate {
    private preds;
    constructor(...preds: Predicate[]);
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
    getClassId(): number;
}
export declare class RegexPredicate extends AbstractPredicate {
    private field;
    private regex;
    constructor(field: string, regex: string);
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
    getClassId(): number;
}
export declare class FalsePredicate extends AbstractPredicate {
    static INSTANCE: FalsePredicate;
    readData(input: DataInput): any;
    writeData(output: DataOutput): any;
    getClassId(): number;
}
export declare class TruePredicate extends AbstractPredicate {
    static INSTANCE: TruePredicate;
    readData(input: DataInput): any;
    writeData(output: DataOutput): any;
    getClassId(): number;
}
export declare class PagingPredicate extends AbstractPredicate {
    private static NULL_ANCHOR;
    private internalPredicate;
    private pageSize;
    private comparatorObject;
    private page;
    private iterationType;
    private anchorList;
    constructor(internalPredicate: Predicate, pageSize: number, comparator: Comparator);
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
    getClassId(): number;
    setIterationType(iterationType: IterationType): void;
    nextPage(): PagingPredicate;
    previousPage(): PagingPredicate;
    setPage(page: number): PagingPredicate;
    setAnchor(page: number, anchor: [any, any]): void;
    getPage(): number;
    getPageSize(): number;
    getNearestAnchorEntry(): [number, [any, any]];
    getIterationType(): IterationType;
    getComparator(): Comparator;
}
