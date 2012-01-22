
package tap.sample;

import java.util.StringTokenizer;
import tap.*;
import CountRec; 

public class WordCount extends Tap {
    
    @Override
    public int run(CommandOptions o) throws Exception {        
        createPhase()
            .reads(o.input).map(WordCountMapper.class).combine(WordCountReducer.class)
            .groupBy("word")
            .writes(o.output).reduce(WordCountReducer.class);
        return make();
    }

    public static class WordCountMapper extends TapMapper {
        @Override
        public void map(String in, Pipe<CountRec> out) {
            StringTokenizer tokenizer = new StringTokenizer(in);
            while (tokenizer.hasMoreTokens()) {
                out.put(CountRec.newBuilder().setWord(tokenizer.nextToken()).setCount(1).build()));
            }
        }
    }

    public static class WordCountReducer extends TapReducer {
        @Override
        public void reduce(Pipe<CountRec> in, Pipe<CountRec> out) {
            String word = null;
            int count = 0;
            for (CountRec rec : in) {
                if (word == null) word = rec.getWord();
                count += rec.getCount();
            }
            out.put(CountRec.newBuilder().setWord(word).setCount(count).build());
        }
    }
}