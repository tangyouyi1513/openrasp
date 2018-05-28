//
// OpenRASP 啄木鸟插件
// 

'use strict'
var plugin  = new RASP('zhuo-mu-niao')

const clean = {
    action:     'ignore',
    message:    '无风险',
    confidence: 0
}

const block = {
    action:     'block',
    message:    '命中啄木鸟规则',
    confidence: 100
}

var rules = {
    command: new RegExp("(\\$\\(owl\\))|(\\$\\{owl\\}|(&{1,2}[\\s\\$]{1,2}owl)|(\\|{1,2}[\\s\\$]{1,2}owl)|(\\n[\\s\\$]{1,2}owl)|(;[\\s\\$]{1,2}owl))|(\\&`'\\|<>\\(\\)\\[\\]\\{\\}\\$)"),
    ssrf: new RegExp("^((http://www\\.10\\.193\\.85\\.22\\.xip\\.io)|(http://10\\.193\\.85\\.22)|(http://[\\.a-z0-9_]*?@10\\.193\\.85\\.22)|(http://180442390:80)|(http://dwz\\.cn/2y0CxR))"),
    sql_query: new RegExp("(owl'\\)E)|(owl'E)|(owl%'E)|(\\(=\\*\\.,;'\")"),
    ognl_expression: [
        '#p=#context.get(\'com.opensymphony.xwork2.dispatcher.HttpServletResponse\').getWriter(),#p.println("f02e6b959bbffc501911e1ee42aece3e"),#p.close()',
        'hacked.vuleye.pw',
        'GRAYBOX_',
    ],
    file_operation: "/etc/passwd"
}

plugin.register('directory', function (params, context) {
    if (params.realpath == "/etc/passwd") {
        return block
    }
    return clean
})

plugin.register('fileUpload', function (params, context) {
    if (params.filename == '/etc/passwd' || params.filename == 'passwd') {
        return block
    }
    return clean
})

// java 只支持 jstl import 方式
plugin.register('include', function (params, context) {
    if (params.realpath == "/etc/passwd") {
        return block
    }
    return clean
})

plugin.register('command', function (params, context) {
    if (rules.command.test(params.command)) {
        return block
    }
    return clean
})

// 为了提高性能，只有当OGNL表达式长度超过30时，才会调用插件
// 这个30可以配置，aka "ognl.expression.minlength"
// https://rasp.baidu.com/doc/setup/others.html
plugin.register('ognl', function (params, context) {
    for (var i = 0; i < rules.ognl_expression.length; i ++ ) {
        if (params.expression.indexOf(rules.ognl_expression[i]) != -) {
            return block
        }
    }

    return clean
})

plugin.register('xxe', function (params, context) {
    // plugin.log('读取XML外部实体: ' + params.entity)
    // php rules.json 没有这个规则
    return clean
})

plugin.register('readFile', function (params, context) {
    if (params.realpath == "/etc/passwd") {
        return block
    }
    return clean
})

plugin.register('writeFile', function (params, context) {
    if (params.realpath == "/etc/passwd") {
        return block
    }
    return clean
})

plugin.register('sql', function (params, context) {
    if (rules.sql_query.test(params.query)) {
        return block
    }
    return clean
})

plugin.log('啄木鸟插件: 初始化成功')


