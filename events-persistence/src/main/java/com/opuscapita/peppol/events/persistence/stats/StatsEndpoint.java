package com.opuscapita.peppol.events.persistence.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.boot.actuate.endpoint.mvc.AbstractMvcEndpoint;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongConsumer;

@Component
public class StatsEndpoint extends AbstractMvcEndpoint {

    public StatsEndpoint() {
        super("/stats", false, true);
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<StatsResponse> fetchStats() {
        List<StatsResponse> result = new ArrayList<>();
        result.add(new StatsResponse("Last message", Instant.ofEpochMilli(Statistics.getLastMessageTimeStamp()).atZone(ZoneId.systemDefault()).toLocalDateTime().toString()));
        result.add(new StatsResponse("Last failed message", Instant.ofEpochMilli(Statistics.getLastFailedMessageTimeStamp()).atZone(ZoneId.systemDefault()).toLocalDateTime().toString()));
        result.add(new StatsResponse("Last successful message", Instant.ofEpochMilli(Statistics.getLastSuccessfulMessageTimeStamp()).atZone(ZoneId.systemDefault()).toLocalDateTime().toString()));
        Averager averager = Statistics.getAggregateTiming().stream().collect(Averager::new, Averager::accept, Averager::combine);
        result.add(new StatsResponse("Average timing for last 100 messages: ",averager.average() + "ms per message"));
        return result;
    }

    @JsonPropertyOrder({"info", "value"})
    public static class StatsResponse {
        @JsonProperty
        private String info;

        @JsonProperty
        private String value;

        public StatsResponse(String info, String value) {
            this.info = info;
            this.value = value;
        }

    }

    class Averager implements LongConsumer
    {
        private int total = 0;
        private int count = 0;

        public double average() {
            return count > 0 ? ((double) total)/count : 0;
        }

        public void accept(long i) { total += i; count++; }
        public void combine(Averager other) {
            total += other.total;
            count += other.count;
        }
    }
}
