import { IdentifiedDataSerializable } from '../serialization/Serializable';
import { Comparator } from './Comparator';
export interface Predicate extends IdentifiedDataSerializable {
}
export declare function sql(str: string): Predicate;
export declare function and(...predicates: Predicate[]): Predicate;
export declare function between(field: string, from: any, to: any): Predicate;
export declare function equal(field: string, value: any): Predicate;
export declare function greaterThan(field: string, value: any): Predicate;
export declare function greaterEqual(field: string, value: any): Predicate;
export declare function lessThan(field: string, value: any): Predicate;
export declare function lessEqual(field: string, value: any): Predicate;
export declare function like(field: string, expr: string): Predicate;
export declare function ilike(field: string, expr: string): Predicate;
export declare function inPredicate(field: string, ...values: any[]): Predicate;
export declare function instanceOf(className: string): Predicate;
export declare function notEqual(field: string, value: any): Predicate;
export declare function not(predic: Predicate): Predicate;
export declare function or(...predicates: Predicate[]): Predicate;
export declare function regex(field: string, reg: string): Predicate;
export declare function alwaysTrue(): Predicate;
export declare function alwaysFalse(): Predicate;
export declare function paging(predicate: Predicate, pageSize: number, comparator?: Comparator): Predicate;
export declare enum IterationType {
    KEY = 0,
    VALUE = 1,
    ENTRY = 2,
}
