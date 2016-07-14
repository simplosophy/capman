package io.capman.common.conf;


import com.google.protobuf.*;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapping a JavaScript to Protobuf Messages
 */
public class JS2PBConfigEvaluator {
    private static Logger LOGGER = LoggerFactory.getLogger(JS2PBConfigEvaluator.class);

    private JS2PBConfigEvaluator(){}

    public static JS2PBConfigEvaluator newInstance() {
        return new JS2PBConfigEvaluator();
    }

    public static class EvalResult{
        Map<String, AbstractMessage > resultMsg = new HashMap<String, AbstractMessage>();

        public <T extends AbstractMessage> T get(String variableName, Class<T> clz){
            return (T) resultMsg.get(variableName);
        }

        public AbstractMessage get(String variableName){
            return resultMsg.get(variableName);
        }
    }

    Context context;
    Scriptable scope;
    String jsStr;

    Map<String, Class<? extends AbstractMessage>> pbClassMap = new HashMap<String, Class<? extends AbstractMessage>>();

    public JS2PBConfigEvaluator jsStr(String js){
        this.jsStr = js;
        return this;
    }

    public <T extends AbstractMessage> JS2PBConfigEvaluator addPbMessage(String variableName, Class<T> msgClass){
        pbClassMap.put(variableName, msgClass);
        return this;
    }

    public EvalResult eval()throws JSConfigFormatException{
        context = Context.enter();
        scope = context.initSafeStandardObjects();
        if(jsStr == null){
            throw new JSConfigFormatException("js script is not set");
        }
        context.evaluateString(scope, jsStr, null, 1, null);
        if(! (scope instanceof Map)){
            throw new JSConfigFormatException("js Script is not valid Config File.");
        }

        Map map = (Map) scope;
        EvalResult result = new EvalResult();
        for (Object s : map.keySet()) {
            if(pbClassMap.containsKey(s)){
                Object dataObj = map.get(s);
                if(!(dataObj instanceof Map)){
                    throw new JSConfigFormatException("Variable " + s + " is not a JS Object");
                }
                try {
                    Class<? extends AbstractMessage> aClass = pbClassMap.get(s);
                    AbstractMessage.Builder<AbstractMessage.Builder> builder =
                            (AbstractMessage.Builder<AbstractMessage.Builder>) aClass.getMethod("newBuilder").invoke(aClass);
                    AbstractMessage.Builder<AbstractMessage.Builder> pbBuilder = js2pb((Map) dataObj, builder);
                    result.resultMsg.put(s.toString(), (AbstractMessage) pbBuilder.buildPartial());

                } catch (Exception e) {
                    throw new JSConfigFormatException(e.getMessage());
                }

            }
        }
        Context.exit();
        return result;
    }


    public Scriptable getScope() {
        return scope;
    }


    private Map<Descriptors.Descriptor, Class> classNameCache = new HashMap<Descriptors.Descriptor, Class>();

    private Class getClassFullNameForMessageDescriptor(Descriptors.Descriptor messageType) throws ClassNotFoundException {
        if(classNameCache.containsKey(messageType)){
            return classNameCache.get(messageType);
        }
        String fullName = messageType.getFullName();
        DescriptorProtos.FileOptions fileOptions = messageType.getFile().getOptions();
        String outerClassname = fileOptions.getJavaOuterClassname();
        if("".equals(outerClassname)){
            String pbfile = messageType.getFile().getFullName();
            int i = pbfile.lastIndexOf('/');
            if(i <0 )i = 0;
            String filenamepart = pbfile.substring(i+1, pbfile.length() - 6);//取文件名
            outerClassname = Character.toUpperCase(filenamepart.toCharArray()[0]) + filenamepart.substring(1);
        }
        int idx = fullName.lastIndexOf('.');
        Class rtn = Class.forName(String.format("%s.%s$%s", fullName.substring(0, idx), outerClassname, fullName.substring(idx + 1)));
        classNameCache.put(messageType, rtn);
        return rtn;
    }

    private Map<Class, Method> builderMethodCache = new HashMap<Class, Method>();
    private Object newBuilderForMsgDescriptor(Descriptors.Descriptor messageType) throws JSConfigFormatException {
        try {
            Class clz = null;
            clz = getClassFullNameForMessageDescriptor(messageType);
            if(!builderMethodCache.containsKey(clz)){
                Method m = clz.getMethod("newBuilder");
                builderMethodCache.put(clz, m);
            }
            return builderMethodCache.get(clz).invoke(clz);
        } catch (Exception e) {
            throw new JSConfigFormatException("Create New Builder Failed.", e);
        }
    }


    /**
     * 将JavaScript对象转换为protobuf的Message
     */
    private <Builder extends AbstractMessage.Builder>
    AbstractMessage.Builder<Builder> js2pb(Map m, AbstractMessage.Builder<Builder> b) throws JSConfigFormatException {
        Descriptors.Descriptor descriptor = b.getDescriptorForType();

        for (Object o : m.keySet()) {
            String key = o.toString();
            Object value = m.get(o);

            Descriptors.FieldDescriptor field = descriptor.findFieldByName(key);

            if(field != null){

                try{
                    switch (field.getType()){
                        case INT32:
                        case SINT32:
                        case SFIXED32:
                        case UINT32:
                        case FIXED32:
                            if(field.isRepeated()){
                                for (int i : JSConfigUtils.value2intArray(value)) {
                                    b.addRepeatedField(field, i);
                                }
                            }else {
                                b.setField(field, JSConfigUtils.value2int(value));
                            }
                            break;

                        case INT64:
                        case SINT64:
                        case SFIXED64:
                        case UINT64:
                        case FIXED64:
                            if(field.isRepeated()){
                                for (long l : JSConfigUtils.value2longArray(value)) {
                                    b.addRepeatedField(field, l);
                                }
                            }else {
                                b.setField(field, JSConfigUtils.value2long(value));
                            }
                            break;

                        case FLOAT:
                            if(field.isRepeated()){
                                for (float v : JSConfigUtils.value2floatArray(value)) {
                                    b.addRepeatedField(field, v);
                                }
                            }else {
                                b.setField(field, JSConfigUtils.value2float(value));
                            }
                            break;
                        case DOUBLE:
                            if(field.isRepeated()){
                                for (double v : JSConfigUtils.value2doubleArray(value)) {
                                    b.addRepeatedField(field, v);
                                }
                            }else {
                                b.setField(field, JSConfigUtils.value2double(value));
                            }
                            break;

                        case BOOL:
                            if(field.isRepeated()){
                                for (boolean b1 : JSConfigUtils.value2boolArray(value)) {
                                    b.addRepeatedField(field, b1);
                                }
                            }else {
                                b.setField(field, JSConfigUtils.value2bool(value));
                            }
                            break;

                        case STRING:
                            if(field.isRepeated()){
                                for (String s : JSConfigUtils.value2StringArray(value)) {
                                    b.addRepeatedField(field, s);
                                }
                            }else {
                                b.setField(field, String.valueOf(value));
                            }
                            break;

                        case BYTES:
                            if(field.isRepeated()){
                                for (ByteString bytes : JSConfigUtils.value2bytesArray(value)) {
                                    b.addRepeatedField(field, bytes);
                                }
                            }else {
                                b.setField(field, JSConfigUtils.value2bytes(value));
                            }
                            break;

                        case ENUM:
                            if(field.isRepeated()){
                                for (Descriptors.EnumValueDescriptor enumValueDescriptor : JSConfigUtils.value2enumArray(value, field.getEnumType())) {
                                    b.addRepeatedField(field, enumValueDescriptor);
                                }
                            }else {
                                b.setField(field, JSConfigUtils.value2enum(value, field.getEnumType()));
                            }
                            break;

                        case MESSAGE:
                        case GROUP:
                            Descriptors.Descriptor messageType = field.getMessageType();

                            if(field.isRepeated()){
                                if(value instanceof List){
                                    List ls = (List) value;
                                    for (Object l : ls) {
                                        if(l instanceof Map){
                                            //递归
                                            Object builder = newBuilderForMsgDescriptor(messageType);
                                            AbstractMessage.Builder<Builder> bb = js2pb((Map) l, (AbstractMessage.Builder<Builder>) builder);
                                            b.addRepeatedField(field, bb.buildPartial());
                                        }else {
                                            throw new JSConfigFormatException("Type Miss Match Field, Require Map. " );
                                        }
                                    }
                                }else {
                                    throw new JSConfigFormatException("Type Miss Match Field, Require List. " );
                                }

                            }else {
                                if(value instanceof Map){
                                    //递归
                                    Object builder = newBuilderForMsgDescriptor(messageType);
                                    AbstractMessage.Builder<Builder> bb = js2pb((Map) value, (AbstractMessage.Builder<Builder>) builder);
                                    b.setField(field, bb.buildPartial());

                                }else {
                                    throw new JSConfigFormatException("Type Miss Match Field, Require Map. " );
                                }

                            }

                            break;

                        default:
                            throw new RuntimeException("Should Not Come Here.");
                    }
                }catch (JSConfigPrecisionException e){
                    LOGGER.warn(e.getMessage() + field.getFullName());
                }catch (JSConfigFormatException e){
                    throw new JSConfigFormatException(e.getMessage() + field.getFullName());
                }
            }
        }

        return b;
    }



}
