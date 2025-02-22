package com.shawn.study.deep.in.flink.window;

import com.shawn.study.deep.in.flink.api.Event;
import com.shawn.study.deep.in.flink.api.source.CustomSourceFunction;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.HashSet;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class UvCountByWindowExample {

  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(1);

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

    // 将数据全部发往同一分区，按窗口统计UV
    stream
        .keyBy(data -> true)
        .window(TumblingEventTimeWindows.of(Time.seconds(10)))
        .process(new UvCountByWindow())
        .print();

    env.execute();
  }

  // 自定义窗口处理函数
  public static class UvCountByWindow
      extends ProcessWindowFunction<Event, String, Boolean, TimeWindow> {
    @Override
    public void process(
        Boolean aBoolean, Context context, Iterable<Event> elements, Collector<String> out)
        throws Exception {
      HashSet<String> userSet = new HashSet<>();
      // 遍历所有数据，放到Set里去重
      for (Event event : elements) {
        userSet.add(event.getUser());
      }
      // 结合窗口信息，包装输出内容
      Long start = context.window().getStart();
      Long end = context.window().getEnd();
      out.collect(
          "窗口: "
              + new Timestamp(start)
              + " ~ "
              + new Timestamp(end)
              + " 的独立访客数量是："
              + userSet.size());
    }
  }
}
