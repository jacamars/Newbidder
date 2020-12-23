/// <reference types="bluebird" />
import * as Promise from 'bluebird';
import HazelcastClient from '../HazelcastClient';
import { BaseProxy } from './BaseProxy';
export declare class PartitionSpecificProxy extends BaseProxy {
    private partitionId;
    constructor(client: HazelcastClient, serviceName: string, name: string);
    protected encodeInvoke<T>(codec: any, ...codecArguments: any[]): Promise<T>;
}
