/// <reference types="bluebird" />
import * as Promise from 'bluebird';
/**
 * Provides initial addresses for client to find and connect to a node
 */
export interface AddressProvider {
    /**
     * @return The possible member addresses to connect to.
     */
    loadAddresses(): Promise<string[]>;
}
