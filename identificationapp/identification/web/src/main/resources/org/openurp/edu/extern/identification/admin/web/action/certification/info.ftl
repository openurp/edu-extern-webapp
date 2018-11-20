[#ftl]
[@b.head/]
  [@b.toolbar title = "学生证书申请情况查看"]
    bar.addItem("返回", function() {
      bg.form.submit(document.searchForm);
    }, "backward.png");
  [/@]
  <table class="infoTable">
    <tr>
      <td class="title" width="10%">学号</td>
      <td>${certification.std.user.code}</td>
      <td class="title" width="10%">姓名</td>
      <td>${certification.std.user.name}</td>
    </tr>
    <tr>
      <td class="title">年级</td>
      <td>${certification.std.state.grade}</td>
      <td class="title">性别</td>
      <td>${certification.std.person.gender.name}</td>
    </tr>
    <tr>
      <td class="title">院系</td>
      <td>${certification.std.state.department.name}</td>
      <td class="title">专业</td>
      <td>${certification.std.state.major.name}</td>
    </tr>
    <tr>
      <td class="title">专业方向</td>
      <td>${(certification.std.state.direction.name)!}</td>
      <td class="title">所在班级</td>
      <td>${certification.std.state.squad.name}</td>
    </tr>
    <tr>
      <td class="title">身份证号</td>
      <td>${certification.std.person.code}</td>
      <td class="title">证书名称</td>
      <td>${certification.certificate.name}</td>
    </tr>
    <tr>
      <td class="title">证书编号</td>
      <td>${certification.code}</td>
      <td class="title">证书成绩</td>
      <td>${certification.score}</td>
    </tr>
    <tr>
      <td class="title">申请人</td>
      <td>${certification.happenBy}</td>
      <td class="title">申请时间</td>
      <td>${certification.happenAt?string("yyyy-MM-dd HH:mm:ss")}</td>
    </tr>
    [#if (certification.lastBy)??]
    <tr>
      <td class="title">分配课程及成绩</td>
      <td colspan="3"></td>
    </tr>
    <tr>
      <td class="title">分配人</td>
      <td>${certification.lastBy}</td>
      <td class="title">申请时间</td>
      <td>${certification.lastAt?string("yyyy-MM-dd HH:mm:ss")}</td>
    </tr>
    [#else]
    <tr>
      <td colspan="4" style="color: red">（当前还未对该证书分配对应的课程与本校成绩）</td>
    </tr>
    [/#if]
  </table>
[@b.foot/]
