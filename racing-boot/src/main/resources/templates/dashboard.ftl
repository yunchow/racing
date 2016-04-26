<#setting classic_compatible = true>
<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Stage Topology Dashboard</title>
    <link rel="stylesheet" href="/bootstrap.min.css" />
    <link rel="stylesheet" href="/matrix-style2.css" />
    <style type="text/css">
        .stage {
            margin: 10px;
            padding: 10px;
            border: 1px solid #D5D5D5;
            width: 290px;
            height: 210px;
            float: left;
        }
        .running, .RUNNABLE {
            color: green;
        }
        .terminated, .TERMINATED, .stoped {
            color: crimson;
        }
        .TIMED_WAITING {
            color: #0044cc;
        }
        a {
            border: 1px solid #DDD;
            padding: 0px 5px;
            background-color: #EEE;
        }
        legend {
            background-color: #EEE;
            border: 1px solid #DDD;
            padding-left: 10px;
        }
        .queue {
            border: 1px solid #DDD;
            padding: 2px 5px;
            background-color: #feffde;
        }
        .size {
            border: 1px solid #EEE;
            padding: 0px 2px;
            background-color: #a9dba9;
        }
        .failed {
            //border: 1px solid #DDD;
            //padding: 2px 5px;
            //background-color: #FBD8DB;
            color: #d58512;
        }
    </style>
</head>
<body>
<div style="width:1330px;text-align:left;margin:10px auto;">
    <fieldset>
        <legend>
            <h1>Stage Topology Dashboard<span class="${top.state}" style="font-size:20px;"> / ${top.state} </span></h1>
            ${top.time?string("yyyy-MM-dd HH:mm:ss")}
        </legend>
        <div>
            <div style="float:right;">
                <span>共${(top.metricses?size)!}个Stage</span>
                <select style="width:auto;" onchange="location.href='/topology/dashboard.do?topName=' + this.value">
                    <#list tops as v>
                        <option value="${v.name}" ${(top.name == v.name) ? string('selected', '')}>${v.name}</option>
                    </#list>
                </select>
            </div>
            <div style="padding: 10px;float:left;">
                <input type="button" value="&nbsp;刷&nbsp;新&nbsp;" onclick="location.reload()">&nbsp;&nbsp;
                <#if top.state == 'running'>
                    <input type="button" value="&nbsp;停&nbsp;止&nbsp;" onclick="location.href='/topology/shutdown/${top.name}.do'">
                </#if>
                <#if top.state == 'terminated'>
                    <input type="button" value="&nbsp;启&nbsp;动&nbsp;" onclick="location.href='/topology/start/${top.name}.do'">
                </#if>
                &nbsp;&nbsp;
                <input type="button" value="&nbsp;重&nbsp;置&nbsp;" onclick="location.href='/topology/reset/${top.name}.do'">
                &nbsp;&nbsp;
                <span style="margin-left:20px;"><input type="checkbox" id="autoReload" style="vertical-align:top;margin-top:6px;">自动刷新</span>

            </div>
            <div style="clear: both"></div>
            <#list top.metricses as metrics>
                <div class="stage">
                    <label style="font-weight: bold;text-align: center;border-bottom: 1px dotted #d0d0d0;padding-bottom: 7px;">${metrics.stageName}</label>
                    <label style="font-weight: bold;text-align: left;">状态：<span class="${metrics.state}" style="">${metrics.state}</span>
                        <span class="${metrics.acceptorState}" style="">/ ${metrics.acceptorState?lower_case}</span></label>
                    启动时间：${metrics.startTime?string("yyyy-MM-dd HH:mm:ss")}<br>
                    并行度：
                        <#if metrics.paralellism gt 1>
                            <a href="/topology/parallelism/${top.name}/${metrics.stageName}/${metrics.paralellism - 1}.do">-</a>
                        </#if>
                        <bold style="font-weight:bold;">${metrics.paralellism}</bold>
                        <a href="/topology/parallelism/${top.name}/${metrics.stageName}/${metrics.paralellism + 1}.do">+</a>
                    &nbsp;&nbsp;
                    并发度：
                        <#if metrics.workSize gt 1>
                            <a href="/topology/worksize/${top.name}/${metrics.stageName}/${metrics.workSize - 1}.do">-</a>
                        </#if>
                        <bold style="font-weight:bold;">${metrics.workSize}</bold>
                        <a href="/topology/worksize/${top.name}/${metrics.stageName}/${metrics.workSize + 1}.do">+</a>
                        <span class="size">${metrics.coreSize}</span>
                        <span class="size">${metrics.maxPoolSize}</span>
                    <br>
                    <span class="queue">任务队列：${metrics.waitingSinkSize}</span>  <span class="queue">工作队列：${metrics.localQueueSize}</span><br>
                    M5: ${metrics.eventCount5} &nbsp; M10: ${metrics.eventCount10} &nbsp; M30: ${metrics.eventCount30}<br>
                    共处理事件数量：${metrics.totalEventCount}<br>
                    成功处理计数：${metrics.successCount}&nbsp;&nbsp;&nbsp;&nbsp;
                    <span class="failed">失败处理计数：${metrics.failedCount}</span><br>
                    <#if metrics.state == 'terminated'>
                        <input type="button" value="&nbsp;启&nbsp;动&nbsp;" onclick="location.href='/topology/start/${top.name}/${metrics.stageName}.do'">
                    </#if>
                    <#if metrics.state == 'running'>
                        <input type="button" value="&nbsp;停&nbsp;止&nbsp;" onclick="location.href='/topology/shutdown/${top.name}/${metrics.stageName}.do'">
                    </#if>
                </div>
            </#list>
        </div>
    </fieldset>
</div>
<script type="text/javascript">
    var autoReload = localStorage.getItem("autoReload");
    if (autoReload == 'true') {
        localStorage.setItem("autoReload", autoReload);
        document.getElementById("autoReload").checked = "checked";
    } else {
        localStorage.setItem("autoReload", document.getElementById("autoReload").checked);
    }

    function reload() {
        if (document.getElementById("autoReload").checked) {
            localStorage.setItem("autoReload", true);
            location.reload();
        } else {
            localStorage.setItem("autoReload", false);
        }
    }
    setInterval("reload();", 3000);
</script>
</body>
</html>