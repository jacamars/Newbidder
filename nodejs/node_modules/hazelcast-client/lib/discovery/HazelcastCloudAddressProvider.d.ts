/// <reference types="bluebird" />
import { AddressProvider } from '../connection/AddressProvider';
import * as Promise from 'bluebird';
import { ILogger } from '../logging/ILogger';
export declare class HazelcastCloudAddressProvider implements AddressProvider {
    private readonly logger;
    private readonly cloudDiscovery;
    constructor(endpointUrl: string, connectionTimeoutMillis: number, logger: ILogger);
    loadAddresses(): Promise<string[]>;
}
