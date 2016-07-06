package io.capman.conf;

import io.capman.protobuf.Common;
import io.capman.util.CommonUtils;
import org.junit.Test;

/**
 * Created by flying on 6/7/16.
 */
public class JSConfigEngineTest {

    @Test
    public void testJs2PbMsg() throws Exception {

        long start = System.currentTimeMillis();
        String js = CommonUtils.resourceAsString("t.js");
        for (int i = 0; i < 1000; i++) {
//            JS2PBConfigEvaluator.EvalResult eval = JS2PBConfigEvaluator.newInstance()
//                    .jsStr(js)
//                    .addPbMessage("o", Common.Test.class)
//                    .addPbMessage("o1", CommonProto.Test.class)
//                    .eval();
//            CommonProto.Test test = eval.get("o", CommonProto.Test.class);
//            CommonProto.Test test1 = eval.get("o1", CommonProto.Test.class);
//            System.out.println(test);
//            System.out.println(test1);
        }

        System.out.println("costs : " +(System.currentTimeMillis() - start));

//        Common.Test.newBuilder().setE1()
//        Object o =JS2PBConfigEvaluator.getInstance().eval(js);
//        Map m = (Map)o;
//        System.out.println(m.keySet());
//        NativeObject no = (NativeObject) o;
//        System.out.println(o);
//        Descriptors.Descriptor descriptorForType = Common.InternalRequest.newBuilder().getDescriptorForType();
//        System.out.println(descriptorForType);
//        Common.InternalResponse.newBuilder().set
//        Descriptors.Descriptor descriptor = Common.InternalRequest.getDescriptor();
//        descriptor.getFile();

//        Common.InternalRequest internalRequest = JS2PBConfigEvaluator.getInstance().js2PbMsg("", Common.InternalRequest.class);
//        System.out.println(internalRequest);

    }


    @Test
    public void test()throws Exception{
//        JS2PBConfigEvaluator.getInstance().init();
//        String js = CommonUtils.resourceAsString("t.js");
//        Object eval = JS2PBConfigEvaluator.getInstance().eval(js);
//        Scriptable scope = JS2PBConfigEvaluator.getInstance().getScope();
//        System.out.println(eval);


    }
}