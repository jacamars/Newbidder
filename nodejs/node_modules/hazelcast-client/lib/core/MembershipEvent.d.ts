import { Member } from './Member';
/**
 * Membership event fired when a new member is added to the cluster and/or when a member leaves the cluster
 * or when there is a member attribute change.
 */
export declare class MembershipEvent {
    /**
     * the removed or added member.
     */
    private member;
    /**
     * the membership event type.
     */
    private eventType;
    /**
     * the members at the moment after this event.
     */
    private members;
    constructor(member: Member, eventType: number, members: Member[]);
}
