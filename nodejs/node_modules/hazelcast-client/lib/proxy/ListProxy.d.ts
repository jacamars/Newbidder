/// <reference types="bluebird" />
import * as Promise from 'bluebird';
import { ItemListener } from '../core/ItemListener';
import { ReadOnlyLazyList } from '../core/ReadOnlyLazyList';
import { IList } from './IList';
import { PartitionSpecificProxy } from './PartitionSpecificProxy';
export declare class ListProxy<E> extends PartitionSpecificProxy implements IList<E> {
    add(element: E): Promise<boolean>;
    addAll(elements: E[]): Promise<boolean>;
    addAllAt(index: number, elements: E[]): Promise<boolean>;
    addAt(index: number, element: E): Promise<void>;
    clear(): Promise<void>;
    contains(entry: E): Promise<boolean>;
    containsAll(elements: E[]): Promise<boolean>;
    isEmpty(): Promise<boolean>;
    remove(entry: E): Promise<boolean>;
    removeAll(elements: E[]): Promise<boolean>;
    retainAll(elements: E[]): Promise<boolean>;
    removeAt(index: number): Promise<E>;
    get(index: number): Promise<E>;
    set(index: number, element: E): Promise<E>;
    indexOf(element: E): Promise<number>;
    lastIndexOf(element: E): Promise<number>;
    size(): Promise<number>;
    subList(start: number, end: number): Promise<ReadOnlyLazyList<E>>;
    toArray(): Promise<E[]>;
    addItemListener(listener: ItemListener<E>, includeValue: boolean): Promise<string>;
    removeItemListener(registrationId: string): Promise<boolean>;
    private serializeList(input);
    private createItemListener(name, includeValue);
}
