import { EntryListener } from './EntryListener';
import { Member } from './Member';
/**
 * An interface which is used to get notified upon a map or an entry event.
 */
export interface MapListener<K, V> extends EntryListener<K, V> {
}
/**
 * A type which is used for map events.
 */
export declare type MapEventListener<K, V> = (mapEvent: MapEvent) => void;
/**
 * Used for map-wide events.
 */
export declare class MapEvent {
    /**
     * The name of the map for this event.
     */
    name: string;
    /**
     * Number of entries affected by this event.
     */
    numberOfAffectedEntries: number;
    /**
     * The member that fired this event.
     */
    member: Member;
    constructor(name: string, numberOfAffectedEntries: number, member: Member);
}
