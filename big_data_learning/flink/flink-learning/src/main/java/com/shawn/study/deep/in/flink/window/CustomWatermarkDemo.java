package com.shawn.study.deep.in.flink.window;

import com.shawn.study.deep.in.flink.api.Event;
import com.shawn.study.deep.in.flink.api.source.CustomSourceFunction;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.TimestampAssigner;
import org.apache.flink.api.common.eventtime.TimestampAssignerSupplier;
import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkGeneratorSupplier;
import org.apache.flink.api.common.eventtime.WatermarkOutput;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class CustomWatermarkDemo {

  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(1);

    env.addSource(new CustomSourceFunction())
        .assignTimestampsAndWatermarks(new CustomWatermarkStrategy())
        .print();

    env.execute();
  }

  public static class CustomWatermarkStrategy implements WatermarkStrategy<Event> {
    @Override
    public TimestampAssigner<Event> createTimestampAssigner(
        TimestampAssignerSupplier.Context context) {
      return new SerializableTimestampAssigner<Event>() {
        @Override
        public long extractTimestamp(Event element, long recordTimestamp) {
          return element.getTimestamp(); // 告诉程序数据源里的时间戳是哪一个字段
        }
      };
    }

    @Override
    public WatermarkGenerator<Event> createWatermarkGenerator(
        WatermarkGeneratorSupplier.Context context) {
      return new CustomPeriodicGenerator();
    }
  }

  public static class CustomPeriodicGenerator implements WatermarkGenerator<Event> {
    private Long delayTime = 5000L; // 延迟时间
    private Long maxTs = Long.MIN_VALUE + delayTime + 1L; // 观察到的最大时间戳

    @Override
    public void onEvent(Event event, long eventTimestamp, WatermarkOutput output) {
      // 每来一条数据就调用一次
      maxTs = Math.max(event.getTimestamp(), maxTs); // 更新最大时间戳
    }

    @Override
    public void onPeriodicEmit(WatermarkOutput output) {
      // 发射水位线，默认200ms调用一次
      output.emitWatermark(new Watermark(maxTs - delayTime - 1L));
    }
  }
}
