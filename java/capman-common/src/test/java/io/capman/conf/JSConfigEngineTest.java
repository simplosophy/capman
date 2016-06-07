package io.capman.conf;

import io.capman.proto.internal.CommonProto;
import io.capman.util.CommonUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by flying on 6/7/16.
 */
public class JSConfigEngineTest {

    @Test
    public void testJs2PbMsg() throws Exception {
        JSConfigEngine.getInstance().init();

        long start = System.currentTimeMillis();
        String js = CommonUtils.resourceAsString("t.js");
        for (int i = 0; i < 1000; i++) {
            CommonProto.Test test = JSConfigEngine.getInstance().js2PbMsg(js, CommonProto.Test.class);
//            System.out.println(test);
        }

        System.out.println("costs : " +(System.currentTimeMillis() - start));

//        Common.Test.newBuilder().setE1()
//        Object o =JSConfigEngine.getInstance().eval(js);
//        Map m = (Map)o;
//        System.out.println(m.keySet());
//        NativeObject no = (NativeObject) o;
//        System.out.println(o);
//        Descriptors.Descriptor descriptorForType = Common.InternalRequest.newBuilder().getDescriptorForType();
//        System.out.println(descriptorForType);
//        Common.InternalResponse.newBuilder().set
//        Descriptors.Descriptor descriptor = Common.InternalRequest.getDescriptor();
//        descriptor.getFile();

//        Common.InternalRequest internalRequest = JSConfigEngine.getInstance().js2PbMsg("", Common.InternalRequest.class);
//        System.out.println(internalRequest);

    }
}