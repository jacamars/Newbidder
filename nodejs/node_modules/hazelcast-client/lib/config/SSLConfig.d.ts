/// <reference types="node" />
import { ConnectionOptions } from 'tls';
import { Properties } from './Properties';
import { ImportConfig } from './ImportConfig';
/**
 * SSL configuration.
 */
export declare class SSLConfig {
    /**
     * If it is true, SSL is enabled.
     */
    enabled: boolean;
    /**
     * sslOptions is by default null which means the following default configuration
     * is used while connecting to the server.
     *
     * {
     *   checkServerIdentity: (): any => null,
     *   rejectUnauthorized: true,
     * };
     *
     * If you want to override the default behavior, you can write your own connection sslOptions.
     */
    sslOptions: ConnectionOptions;
    /**
     * sslOptionsFactoryConfig is config for ssl options factory. If you don't specify the path, BasicSSLOptionsFactory is used
     * by default.
     */
    sslOptionsFactoryConfig: ImportConfig;
    /**
     * sslOptionsFactoryProperties is the properties to be set for ssl options.
     */
    sslOptionsFactoryProperties: Properties;
}
