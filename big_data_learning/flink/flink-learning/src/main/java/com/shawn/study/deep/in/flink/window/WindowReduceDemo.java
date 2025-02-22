package com.shawn.study.deep.in.flink.window;

import com.shawn.study.deep.in.flink.api.Event;
import com.shawn.study.deep.in.flink.api.source.CustomSourceFunction;
import java.time.Duration;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

public class WindowReduceDemo {

  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(1);

    // 从自定义数据源读取数据，并提取时间戳、生成水位线
    SingleOutputStreamOperator<Event> stream =
        env.addSource(new CustomSourceFunction())
            .assignTimestampsAndWatermarks(
                WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO)
                    .withTimestampAssigner(
                        new SerializableTimestampAssigner<Event>() {
                          @Override
                          public long extractTimestamp(Event element, long recordTimestamp) {
                            return element.getTimestamp();
                          }
                        }));
    stream
        .map(
            new MapFunction<Event, Tuple2<String, Long>>() {
              @Override
              public Tuple2<String, Long> map(Event value) throws Exception {
                // 将数据转换成二元组，方便计算
                return Tuple2.of(value.getUser(), 1L);
              }
            })
        .keyBy(r -> r.f0)
        // 设置滚动事件时间窗口
        .window(TumblingEventTimeWindows.of(Time.seconds(5)))
        .reduce(
            new ReduceFunction<Tuple2<String, Long>>() {
              @Override
              public Tuple2<String, Long> reduce(
                  Tuple2<String, Long> value1, Tuple2<String, Long> value2) throws Exception {
                // 定义累加规则，窗口闭合时，向下游发送累加结果
                return Tuple2.of(value1.f0, value1.f1 + value2.f1);
              }
            })
        .print();

    env.execute();
  }
}
