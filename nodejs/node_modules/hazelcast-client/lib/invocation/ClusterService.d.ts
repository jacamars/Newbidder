/// <reference types="bluebird" />
import { ClientConnection } from './ClientConnection';
import * as Promise from 'bluebird';
import { Member } from '../core/Member';
import { ClientInfo } from '../ClientInfo';
import HazelcastClient from '../HazelcastClient';
import { MemberSelector } from '../core/MemberSelector';
import { MembershipListener } from '../core/MembershipListener';
export declare enum MemberEvent {
    ADDED = 1,
    REMOVED = 2,
}
/**
 * Manages the relationship of this client with the cluster.
 */
export declare class ClusterService {
    /**
     * The unique identifier of the owner server node. This node is responsible for resource cleanup
     */
    ownerUuid: string;
    /**
     * The unique identifier of this client instance. Assigned by owner node on authentication
     */
    uuid: string;
    private knownAddresses;
    private members;
    private client;
    private ownerConnection;
    private membershipListeners;
    private logger;
    constructor(client: HazelcastClient);
    /**
     * Starts cluster service.
     * @returns
     */
    start(): Promise<void>;
    /**
     * Connects to cluster. It uses the addresses provided in the configuration.
     * @returns
     */
    connectToCluster(): Promise<void>;
    getPossibleMemberAddresses(): Promise<string[]>;
    /**
     * Returns the list of members in the cluster.
     * @returns
     */
    getMembers(selector?: MemberSelector): Member[];
    getMember(uuid: string): Member;
    /**
     * Returns the number of nodes in cluster.
     * @returns {number}
     */
    getSize(): number;
    /**
     * Returns information about this client.
     * @returns {ClientInfo}
     */
    getClientInfo(): ClientInfo;
    /**
     * Returns the connection associated with owner node of this client.
     * @returns {ClientConnection}
     */
    getOwnerConnection(): ClientConnection;
    /**
     * Adds MembershipListener to listen for membership updates. There is no check for duplicate registrations,
     * so if you register the listener twice, it will get events twice.
     * @param {MembershipListener} The listener to be registered
     * @return The registration ID
     */
    addMembershipListener(membershipListener: MembershipListener): string;
    /**
     * Removes registered MembershipListener.
     * @param {string} The registration ID
     * @return {boolean} true if successfully removed, false otherwise
     */
    removeMembershipListener(registrationId: string): boolean;
    initMembershipListener(): Promise<void>;
    private initHeartbeatListener();
    private initConnectionListener();
    private onConnectionClosed(connection);
    private onHeartbeatStopped(connection);
    private tryConnectingToAddresses(index, remainingAttemptLimit, attemptPeriod, cause?);
    private handleMember(member, eventType);
    private handleMemberList(members);
    private handleMemberAttributeChange(uuid, key, operationType, value);
    private memberAdded(member);
    private memberRemoved(member);
}
