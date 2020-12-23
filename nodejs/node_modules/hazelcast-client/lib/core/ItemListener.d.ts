import { Member } from './Member';
/**
 * Item listener for IQueue, ISet, IList.
 */
export interface ItemListener<E> {
    itemAdded?: ItemEventListener<E>;
    itemRemoved?: ItemEventListener<E>;
}
/**
 * A type which is used for item events.
 */
export declare type ItemEventListener<E> = (itemEvent: ItemEvent<E>) => void;
/**
 * IQueue, ISet, IList item event.
 */
export declare class ItemEvent<E> {
    /**
     * The name of the data structure for this event.
     */
    name: string;
    /**
     * The value of the item event.
     */
    item: E;
    /**
     * The event type.
     */
    eventType: ItemEventType;
    /**
     * The member that fired this event.
     */
    member: Member;
    constructor(name: string, itemEventType: ItemEventType, item: E, member: Member);
}
export declare enum ItemEventType {
    ADDED = 1,
    REMOVED = 2,
}
