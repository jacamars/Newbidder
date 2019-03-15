/// <reference types="bluebird" />
import { AddressProvider } from './AddressProvider';
import { ClientNetworkConfig } from '../config/ClientNetworkConfig';
import * as Promise from 'bluebird';
/**
 * Default address provider of Hazelcast.
 *
 * Loads addresses from the Hazelcast configuration.
 */
export declare class DefaultAddressProvider implements AddressProvider {
    private networkConfig;
    private readonly noOtherAddressProviderExist;
    constructor(networkConfig: ClientNetworkConfig, noOtherAddressProviderExist: boolean);
    loadAddresses(): Promise<string[]>;
}
