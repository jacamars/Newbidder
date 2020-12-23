/// <reference types="bluebird" />
import Address = require('../Address');
import * as Promise from 'bluebird';
import { Properties } from '../config/Properties';
/**
 * Discovery service that discover nodes via hazelcast.cloud
 * https://coordinator.hazelcast.cloud/cluster/discovery?token=<TOKEN>
 */
export declare class HazelcastCloudDiscovery {
    /**
     * Internal client property to change base url of cloud discovery endpoint.
     * Used for testing cloud discovery.
     */
    private static readonly CLOUD_URL_BASE_PROPERTY;
    private static readonly CLOUD_URL_PATH;
    private static readonly PRIVATE_ADDRESS_PROPERTY;
    private static readonly PUBLIC_ADDRESS_PROPERTY;
    private readonly endpointUrl;
    private readonly connectionTimeoutInMillis;
    constructor(endpointUrl: string, connectionTimeoutInMillis: number);
    static createUrlEndpoint(properties: Properties, cloudToken: string): string;
    discoverNodes(): Promise<Map<string, Address>>;
    callService(): Promise<Map<string, Address>>;
    private parseResponse(data);
}
