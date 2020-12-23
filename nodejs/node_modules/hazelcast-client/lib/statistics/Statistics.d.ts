import HazelcastClient from '../HazelcastClient';
import { ClientConnection } from '../invocation/ClientConnection';
import { Task } from '../Util';
/**
 * This class is the main entry point for collecting and sending the client
 * statistics to the cluster. If the client statistics feature is enabled,
 * it will be scheduled for periodic statistics collection and sent.
 */
export declare class Statistics {
    static readonly PERIOD_SECONDS_DEFAULT_VALUE: number;
    private static readonly ENABLED;
    private static readonly PERIOD_SECONDS;
    private static readonly NEAR_CACHE_CATEGORY_PREFIX;
    private static readonly FEATURE_SUPPORTED_SINCE_VERSION_STRING;
    private static readonly FEATURE_SUPPORTED_SINCE_VERSION;
    private static readonly STAT_SEPARATOR;
    private static readonly KEY_VALUE_SEPARATOR;
    private static readonly ESCAPE_CHAR;
    private static readonly EMPTY_STAT_VALUE;
    private readonly allGauges;
    private readonly enabled;
    private readonly properties;
    private readonly logger;
    private client;
    private ownerAddress;
    private task;
    constructor(clientInstance: HazelcastClient);
    /**
     * Registers all client statistics and schedules periodic collection of stats.
     */
    start(): void;
    stop(): void;
    /**
     * @param periodSeconds the interval at which the statistics collection and send is being run
     */
    schedulePeriodicStatisticsSendTask(periodSeconds: number): Task;
    sendStats(newStats: string, ownerConnection: ClientConnection): void;
    /**
     * @return the owner connection to the server for the client only if the server supports the client statistics feature
     */
    private getOwnerConnection();
    private registerMetrics();
    private registerGauge(gaugeName, gaugeFunc);
    private addStat(stats, name, value, keyPrefix?);
    private addEmptyStat(stats, name, keyPrefix);
    private fillMetrics(stats, ownerConnection);
    private getNameWithPrefix(name);
    private escapeSpecialCharacters(buffer, start);
    private addNearCacheStats(stats);
}
