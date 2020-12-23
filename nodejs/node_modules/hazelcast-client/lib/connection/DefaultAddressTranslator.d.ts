/// <reference types="bluebird" />
import { AddressTranslator } from './AddressTranslator';
import * as Promise from 'bluebird';
import Address = require('../Address');
/**
 * Default Address Translator is a no-op. It always returns the given address.
 */
export declare class DefaultAddressTranslator implements AddressTranslator {
    refresh(): Promise<void>;
    translate(address: Address): Promise<Address>;
}
