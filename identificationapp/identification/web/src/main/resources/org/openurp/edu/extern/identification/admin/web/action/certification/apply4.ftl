[#ftl]
[@b.head/]
  [@b.toolbar title = "学生证书申请－<span style=\"color:blue\">第<span style=\"padding-left: 3px; padding-right: 3px\">4</span>步：获取/保存证书结果</span>"]
    bar.addItem("取消返回", function() {
      bg.form.submit(document.searchForm);
    }, "backward.png");
  [/@]
  [#if (certifications?size)?default(0) == 0]
    [@b.div id="applyResult"/]
    <script>
      $(function() {
        function request(displayObj) {
          var dataMap = {};
          [#list outerValueMap?keys as key]
          dataMap.${key} = "${outerValueMap[key]}";
          [/#list]

          $.ajax({
            "type": "POST",
            "url": "${setting.url?js_string}",
            "async": false,
            "dataType": "jsonp",[#--FIXME 2018-04-13 zhouqi 假设返回的结果格式为json--]
                                [#--跨域访问--]
            "data": dataMap,
            "jsonpCallback": "certData",[#--由于是跨域访问，必须指定回调函数名--]
            "success": function(data) {
              [#--console.log(data);--]

              var param = {};

              param.settingId = "${setting.id}";
              param.stdId = "${std.id}";
              param.certificateId = "${certificate.id}"; [#-- 证书大类 --]

              [#-- 检测是否反馈值为空：如果为空，则本学生确实没有取得该证书，否则确实取得了 --]
              [#-- FIXME 2018-06-18 zhouqi 这个具体如何验证是否获取证书的过程，可以根据具体情况作相应的调整 --]
              var value = "";

              for (var field in data) {
                var v = data[field];
                if (null == v) {
                  v = "";
                }
                v = v.replace(/\s/gm, "");

                value += v;
              }

              if (v.length > 0) {
                param = Object.assign(param, data);
              } else {
                [#list innerValues as value]
                param.${value[0]}Id = "${value[1]}"; [#-- 证书明细 --]
                [/#list]
              }

              [#--2018-05-14 zhouqi 把返回的结果数据作进一步处理--]
              $.ajax({
                "type": "POST",
                "url": "${b.url("!apply4ResultAjax")}",
                "async": false,
                "dataType": "html",
                "data": param,
                "success": function(data) {
                  displayObj.html(data);
                }
              });
            },
            "error": function(request, errMsg, e) {
              var errDataMap = {};
              errDataMap.request = {};
              errDataMap.request.readyState = request.readyState;
              errDataMap.request.responseText = request.responseText;
              errDataMap.request.responseXML = request.responseXML;
              errDataMap.request.status = request.status;
              errDataMap.request.statusText = request.statusText;

              errDataMap.errMsg = errMsg;

              errDataMap.e = e;

              console.log(errDataMap);

              [#--
              var divObj = $("<div>");
              divObj.html("证书获取失败！原因可能是证书获取地址、数据格式等变更了，或者证书获取通道停用未开启。请联系校方有关部门，电话xxx-xxxxxxx。");
              displayObj.append(divObj);
              displayObj.append(request.responseText);
              --]
              displayObj.append("证书获取失败！原因可能是证书获取地址、数据格式等变更了，或者证书获取通道停用未开启。请联系校方有关部门，电话xxx-xxxxxxx。");
              displayObj.css("color", "red");
            }
          });
        }

        $(document).ready(function() {
          var applyResultObj = $("div#applyResult");

          request(applyResultObj);
        });
      });
    </script>
  [#else]
    <div style="color: blue">学号为${std.user.code}的${std.user.name}同学已经获取该证书，无需再次申请，证书如下：</div>
    [@b.grid items=certifications var="certification" sortable="false"]
      [@b.row]
        [@b.col title="证书代码" property="certificate.code"/]
        [@b.col title="证书名称" property="certificate.name"/]
        [@b.col title="报考省份" property="certificate.division.code"]${(certification.certificate.division.code[0..1] + "-" + certification.certificate.division.name)!"全国"}[/@]
        [@b.col title="入库人" property="happenBy"]${certification.happenBy}[/@]
        [@b.col title="入库时间" property="happenAt"]${certification.happenAt?string("yyyy-MM-dd")}[/@]
      [/@]
    [/@]
  [/#if]
  [#--
  [#include "/component/certificate/selector2.ftl"/]
  [@b.form name="certificationForm" action="!apply" target="certifications" theme="list"]
    [@b.field label="学号"]<span>${std.user.code}</span>[/@]
    [@b.field label="姓名"]<span>${std.user.name}</span>[/@]
    [@b.field label="年级"]<span>${std.state.grade}</span>[/@]
    [@b.field label="院系"]<span>${std.state.department.name}</span>[/@]
    [@b.field label="专业"]<span>${std.state.major.name}</span>[/@]
    [@b.field label="方向"]<span>${(std.state.direction.name)!"<br>"}</span>[/@]
    [@b.field label="班级"]<span>${(std.state.adminclass.name)!"<br>"}</span>[/@]
    [@b.field label="身份证号"]<span>${std.person.code}</span>[/@]
    [@certSelects "certificationForm" "width: 200px"/]
    [@b.formfoot]
      <input type="hidden" name="no" value="${std.user.code}"/>
      <input type="hidden" name="stdId" value="${std.id}"/>
      <input type="hidden" name="keys" value="${keys?join(",")}"/>
      <input type="hidden" name="settingId" value="${setting.id}"/>
      [@b.submit value="下一步"/]
    [/@]
  [/@]
  --]
[@b.foot/]
