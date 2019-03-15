import { CountAggregator, DoubleAverageAggregator, DoubleSumAggregator, FixedPointSumAggregator, FloatingPointSumAggregator, IntegerAverageAggregator, IntegerSumAggregator, LongAverageAggregator, LongSumAggregator, MaxAggregator, MinAggregator, NumberAverageAggregator } from './Aggregator';
/**
 * @param attributePath extracts values from this path if given
 * @return an aggregator that counts the input values.
 * Accepts nulls as input values.
 * Aggregation result type Long.
 */
export declare function count(attributePath?: string): CountAggregator;
/**
 * @param attributePath extracts values from this path if given
 * @return an aggregator that calculates the average of the input values.
 * Does NOT accept null input values.
 * Accepts only Double input values (primitive and boxed).
 * Aggregation result type is number.
 */
export declare function doubleAvg(attributePath?: string): DoubleAverageAggregator;
/**
 * @param attributePath extracts values from this path if given
 * @return an aggregator that calculates the sum of the input values.
 * Does NOT accept null input values.
 * Accepts only Double input values (primitive and boxed).
 * Aggregation result type is Double.
 */
export declare function doubleSum(attributePath?: string): DoubleSumAggregator;
/**
 * @param attributePath extracts values from this path if given
 * @return an aggregator that calculates the average of the input values.
 * Does NOT accept null input values.
 * Accepts generic Number input values.
 * Aggregation result type is Double.
 */
export declare function numberAvg(attributePath?: string): NumberAverageAggregator;
/**
 * @param attributePath extracts values from this path if given
 * @return an aggregator that calculates the sum of the input values.
 * Does NOT accept null input values.
 * Accepts generic Number input values.
 * Aggregation result type is {Long}.
 */
export declare function fixedPointSum(attributePath?: string): FixedPointSumAggregator;
/**
 * @param attributePath extracts values from this path if given
 * @return an aggregator that calculates the sum of the input values.
 * Does NOT accept null input values.
 * Accepts generic Number input values.
 * Aggregation result type is number.
 */
export declare function floatingPointSum(attributePath?: string): FloatingPointSumAggregator;
/**
 * @param attributePath extracts values from this path if given
 * @param <R> type of the input object.
 * @return an aggregator that calculates the max of the input values.
 * Accepts null input values.
 * Aggregation result type is <R>
 */
export declare function max<R>(attributePath?: string): MaxAggregator<R>;
/**
 * @param attributePath extracts values from this path if given
 * @param <R> type of the input object.
 * @return an aggregator that calculates the min of the input values.
 * Accepts null input values.
 * Aggregation result type is <R>
 */
export declare function min<R>(attributePath?: string): MinAggregator<R>;
/**
 * @param attributePath extracts values from this path if given
 * @return an aggregator that calculates the average of the input values.
 * Does NOT accept null input values.
 * Accepts only Integer input values (primitive and boxed).
 * Aggregation result type is number.
 */
export declare function integerAvg(attributePath?: string): IntegerAverageAggregator;
/**
 * @param attributePath extracts values from this path if given
 * @return an aggregator that calculates the sum of the input values.
 * Does NOT accept null input values.
 * Accepts only Integer input values (primitive and boxed).
 * Aggregation result type is {Long}.
 */
export declare function integerSum(attributePath?: string): IntegerSumAggregator;
/**
 * @param attributePath extracts values from this path if given
 * @return an aggregator that calculates the average of the input values.
 * Does NOT accept null input values.
 * Accepts only Long input values (primitive and boxed).
 * Aggregation result type is number.
 */
export declare function longAvg(attributePath?: string): LongAverageAggregator;
/**
 * @param attributePath extracts values from this path if given
 * @return an aggregator that calculates the sum of the input values.
 * Does NOT accept null input values.
 * Accepts only Long input values (primitive and boxed).
 * Aggregation result type is {Long}.
 */
export declare function longSum(attributePath?: string): LongSumAggregator;
