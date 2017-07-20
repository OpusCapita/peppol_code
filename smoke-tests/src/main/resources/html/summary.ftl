<html>
<head>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css" rel="stylesheet">
    <title>Tests summary</title>
</head>
<body>
<div style="margin-right: 15px; margin-left: 15px; margin-top: 15px">

    <div>
        <h3>Summary</h3>
        <div class="panel panel-info">
            <div class="panel-heading">
                <h3 class="panel-title">All tests</h3>
            </div>
            <div class="panel-body">
                <div class="row">
                    <div class="col-xs-6">
                        <table table class="table table-bordered table-hover">
                            <tr class="info">
                                <th>Result</th>
                                <th>All tests</th>
                                <th>Successful</th>
                                <th>Failed</th>
                            </tr>
                            <tr>
                                <td>${result}</td>
                                <td>${all_tests}</td>
                                <td>${successful_tests}</td>
                                <td>${failed_tests}</td>
                            </tr>
                        </table>

                    </div>

                    <div class="col-xs-6">
                        <h3>${percentage} % passed</h3>
                        <div class="progress">
                            <div class="progress-bar" role="progressbar" aria-valuenow="${successful_tests}"
                                 aria-valuemin="0" aria-valuemax="${all_tests}" style="width: ${percentage}%">
                                <span>${percentage} % passed</span></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div>
    <#if timings?has_content>
        <h3>Test timings</h3>
        <div>
            <div class="panel panel-info">
                <div class="panel-heading" style="padding:5; margin:0;">
                    <h3 class="panel-title">Test Timings</h3>
                </div>
                <div class="panel-body">
                    <table class="table table-bordered table-hover table-condensed" style="padding:0; margin:0;">
                        <tr>
                            <th>Name</th>
                            <th>Start</th>
                            <th>End</th>
                            <th>Terminated</th>
                            <th>Start to End</th>
                            <th>Start to Terminated</th>
                        </tr>
                        <#list timings as time>
                            <tr>
                                <td>${time.name}</td>
                                <td>${time.start}</td>
                                <td>${time.end}</td>
                                <td>${time.terminated}</td>
                                <td>${time.start_to_end}</td>
                                <td>${time.start_to_terminated}</td>
                            </tr>
                        </#list>
                    </table>
                </div>
            </div>
        </div>
    </#if>
    </div>

    <div>
    <#if fails?has_content>
        <h3>Failed tests</h3>
        <#list fails as item>
            <div>
                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">${item.name}</h3>
                    </div>
                    <div class="panel-body">
                        <#list item.value as f>

                            <table class="table table-bordered table-hover table-condensed">
                                <tr>
                                    <th style="width: 210px;">Test</th>
                                    <th style="width: 150px;">Output</th>
                                    <th style="width: 150px;">Expected</th>
                                    <th>Details</th>
                                </tr>
                                <#list f.details.asserts as a>
                                    <tr>
                                        <td>${a.name}</td>
                                        <td>${a.output_value}</td>
                                        <td>${a.expected_result}</td>
                                        <td>${a.comment}</td>
                                    </tr>
                                </#list>
                            </table>
                        </#list>
                    </div>
                </div>
            </div>
        </#list>
    </#if>
    </div>

    <div>
    <#if oks?has_content>
        <h3>Succeed tests</h3>
        <#list oks as item>
            <div>
                <div class="panel panel-info">
                    <div class="panel-heading" style="padding:5; margin:0;">
                        <h3 class="panel-title">${item.name}</h3>
                    </div>
                    <div class="panel-body">
                        <#list item.value as s>

                            <table class="table table-bordered table-hover table-condensed"
                                   style="padding:0; margin:0;">
                                <tr>
                                    <th style="width: 210px;">Test</th>
                                    <th style="width: 150px;">Test result</th>
                                    <th>Details</th>
                                </tr>
                                <#list s.details.asserts as d>
                                    <tr>
                                        <td>${d.name}</td>
                                        <td>${d.output_value}</td>
                                        <td>${d.comment}</td>
                                    </tr>
                                </#list>
                            </table>
                        </#list>
                    </div>
                </div>
            </div>
        </#list>
    </#if>
    </div>
</div>
</body>
</html>