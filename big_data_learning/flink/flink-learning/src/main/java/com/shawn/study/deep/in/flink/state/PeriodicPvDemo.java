package com.shawn.study.deep.in.flink.state;

import com.shawn.study.deep.in.flink.api.Event;
import com.shawn.study.deep.in.flink.api.source.CustomSourceFunction;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class PeriodicPvDemo {

  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(1);

    SingleOutputStreamOperator<Event> stream =
        env.addSource(new CustomSourceFunction())
            .assignTimestampsAndWatermarks(
                WatermarkStrategy.<Event>forMonotonousTimestamps()
                    .withTimestampAssigner(
                        new SerializableTimestampAssigner<Event>() {
                          @Override
                          public long extractTimestamp(Event element, long recordTimestamp) {
                            return element.getTimestamp();
                          }
                        }));

    stream.print("input");

    // 统计每个用户的pv，隔一段时间（10s）输出一次结果
    stream.keyBy(Event::getUser).process(new PeriodicPvResult()).print();

    env.execute();
  }

  // 注册定时器，周期性输出pv
  public static class PeriodicPvResult extends KeyedProcessFunction<String, Event, String> {
    // 定义两个状态，保存当前pv值，以及定时器时间戳
    ValueState<Long> countState;
    ValueState<Long> timerTsState;

    @Override
    public void open(Configuration parameters) throws Exception {
      countState =
          getRuntimeContext().getState(new ValueStateDescriptor<Long>("count", Long.class));
      timerTsState =
          getRuntimeContext().getState(new ValueStateDescriptor<Long>("timerTs", Long.class));
    }

    @Override
    public void processElement(Event value, Context ctx, Collector<String> out) throws Exception {
      // 更新count值
      Long count = countState.value();
      if (count == null) {
        countState.update(1L);
      } else {
        countState.update(count + 1);
      }
      // 注册定时器
      if (timerTsState.value() == null) {
        ctx.timerService().registerEventTimeTimer(value.getTimestamp() + 10 * 1000L);
        timerTsState.update(value.getTimestamp() + 10 * 1000L);
      }
    }

    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out)
        throws Exception {
      out.collect(ctx.getCurrentKey() + " pv: " + countState.value());
      // 清空状态
      timerTsState.clear();
    }
  }
}
