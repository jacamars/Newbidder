/// <reference types="bluebird" />
import * as Promise from 'bluebird';
import { ItemListener } from '../core/ItemListener';
import { ISet } from './ISet';
import { PartitionSpecificProxy } from './PartitionSpecificProxy';
export declare class SetProxy<E> extends PartitionSpecificProxy implements ISet<E> {
    add(entry: E): Promise<boolean>;
    addAll(items: E[]): Promise<boolean>;
    toArray(): Promise<E[]>;
    clear(): Promise<void>;
    contains(entry: E): Promise<boolean>;
    containsAll(items: E[]): Promise<boolean>;
    isEmpty(): Promise<boolean>;
    remove(entry: E): Promise<boolean>;
    removeAll(items: E[]): Promise<boolean>;
    retainAll(items: E[]): Promise<boolean>;
    size(): Promise<number>;
    addItemListener(listener: ItemListener<E>, includeValue?: boolean): Promise<string>;
    removeItemListener(registrationId: string): Promise<boolean>;
    private serializeList(input);
    private createEntryListener(name, includeValue);
}
