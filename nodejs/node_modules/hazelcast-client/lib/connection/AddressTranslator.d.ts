/// <reference types="bluebird" />
import Address = require('../Address');
import * as Promise from 'bluebird';
/**
 *  Address Translator is used for resolve private ip
 *  addresses of cloud services.
 */
export interface AddressTranslator {
    /**
     * Translates the given address to another address specific to
     * network or service
     *
     * @param address
     * @return new address if given address is known, otherwise return null
     */
    translate(address: Address): Promise<Address>;
    /**
     * Refreshes the internal lookup table if necessary.
     */
    refresh(): Promise<void>;
}
