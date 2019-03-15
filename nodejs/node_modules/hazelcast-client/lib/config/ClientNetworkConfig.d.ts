import { ClientCloudConfig } from './ClientCloudConfig';
import { SSLConfig } from './SSLConfig';
/**
 * Network configuration.
 */
export declare class ClientNetworkConfig {
    /**
     * Client tries to connect the members at these addresses.
     */
    addresses: string[];
    /**
     * hazelcast.cloud configuration to let the client connect the cluster via hazelcast.cloud
     */
    cloudConfig: ClientCloudConfig;
    /**
     * While client is trying to connect initially to one of the members in the {@link addresses},
     * all might be not available. Instead of giving up, throwing Exception and stopping client, it will
     * attempt to retry as much as {@link connectionAttemptLimit} times.
     */
    connectionAttemptLimit: number;
    /**
     * Period for the next attempt to find a member to connect.
     */
    connectionAttemptPeriod: number;
    /**
     * Timeout value in millis for nodes to accept client connection requests.
     */
    connectionTimeout: number;
    /**
     * true if redo operations are enabled (not implemented yet)
     */
    redoOperation: boolean;
    /**
     * If true, client will behave as smart client instead of dummy client. Smart client sends key based operations
     * to owner of the keys. Dummy client sends all operations to a single node. See http://docs.hazelcast.org to
     * learn about smart/dummy client.
     */
    smartRouting: boolean;
    /**
     * SSL configuration.
     */
    sslConfig: SSLConfig;
}
