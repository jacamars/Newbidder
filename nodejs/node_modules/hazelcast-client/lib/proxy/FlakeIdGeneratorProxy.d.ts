/// <reference types="bluebird" />
/// <reference types="long" />
import * as Promise from 'bluebird';
import * as Long from 'long';
import HazelcastClient from '../HazelcastClient';
import { BaseProxy } from './BaseProxy';
import { FlakeIdGenerator } from './FlakeIdGenerator';
export declare class FlakeIdGeneratorProxy extends BaseProxy implements FlakeIdGenerator {
    private autoBatcher;
    private config;
    constructor(client: HazelcastClient, serviceName: string, name: string);
    newId(): Promise<Long>;
}
