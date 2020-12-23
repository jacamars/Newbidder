/// <reference types="bluebird" />
import { AddressTranslator } from '../connection/AddressTranslator';
import * as Promise from 'bluebird';
import Address = require('../Address');
import { ILogger } from '../logging/ILogger';
export declare class HazelcastCloudAddressTranslator implements AddressTranslator {
    private logger;
    private readonly hazelcastCloudDiscovery;
    private privateToPublic;
    constructor(endpointUrl: string, connectionTimeoutMillis: number, logger: ILogger);
    translate(address: Address): Promise<Address>;
    refresh(): Promise<void>;
}
