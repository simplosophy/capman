//a = 123;
//b = 456;
//d = 3221225472;
//e = "1234";
//
//f = {
//    fa : 123
//}
var f = function(a, b){
    return a+b;
};

o={
    "a":f(1,2),
    "b":0x3221225472,
    "c":{
        "e":"abc"
    },
    "d":0x123,
    "array":"aaaaaa",
    "array2":[[123,2,4,2], "aaaa"],
    "e1":1,
    "e2":"ETest_2",
    "ss":["1","2","3"],
    "rtm":[
        {
            "e":124,
            "rm":[{"e":"ass"}]
        },{"e":"1235"}
    ]
}
