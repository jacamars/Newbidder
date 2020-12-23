import { Member } from './Member';
/**
 * Event for member attribute changes.
 */
export declare class MemberAttributeEvent {
    /**
     * the member for this MemberAttributeEvent.
     */
    member: Member;
    /**
     * the key for this MemberAttributeEvent.
     */
    key: string;
    /**
     * the type of member attribute change for this MemberAttributeEvent.
     */
    operationType: MemberAttributeOperationType;
    /**
     * the value for this MemberAttributeEvent.
     */
    value: string;
    constructor(member: Member, key: string, operationType: MemberAttributeOperationType, value: string);
}
/**
 * Used to identify the type of member attribute change, either PUT or REMOVED.
 *
 */
export declare enum MemberAttributeOperationType {
    /**
     * Indicates an attribute being put.
     */
    PUT = 1,
    /**
     * Indicates an attribute being removed.
     */
    REMOVE = 2,
}
