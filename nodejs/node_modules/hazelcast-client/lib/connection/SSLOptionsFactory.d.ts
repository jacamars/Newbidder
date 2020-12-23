/// <reference types="bluebird" />
import * as Promise from 'bluebird';
import { Properties } from '../config/Properties';
export interface SSLOptionsFactory {
    init(properties: Properties): Promise<void>;
    getSSLOptions(): any;
}
