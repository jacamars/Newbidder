import { MemberAttributeEvent } from './MemberAttributeEvent';
import { MembershipEvent } from './MembershipEvent';
/**
 * Cluster membership listener.
 */
export interface MembershipListener {
    /**
     * Invoked when a new member is added to the cluster.
     * @param {MembershipEvent} membership event
     */
    memberAdded(membership: MembershipEvent): void;
    /**
     * Invoked when an existing member leaves the cluster.
     * @param {MembershipEvent} membership event when an existing member leaves the cluster
     */
    memberRemoved(membership: MembershipEvent): void;
    /**
     * Invoked when an attribute of a member was changed.
     * @param {MemberAttributeEvent} member attribute event when an attribute of a member was changed
     */
    memberAttributeChanged(memberAttributeEvent: MemberAttributeEvent): void;
}
