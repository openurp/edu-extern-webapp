[#ftl/]
[@b.head/]
  [@b.toolbar title="证书认定标准配置查看"]
    bar.addBack();
  [/@]
  <style>
    .infoTable .gridtable {width: 80%;border-collapse: collapse;border:solid;border-width:1px;border-color:#006CB2;vertical-align: middle;table-layout:fixed}

    .infoTable .gridtable td{border-color:#006CB2;border-style:solid;border-width:0 1px 1px 0;overflow:hidden;word-wrap:break-word;}

    .infoTable .gridtable th{border-color:#006CB2;border-style:solid;border-width:0 1px 1px 0;height: initial;}

    .infoTable .gridhead{color: #000000;text-decoration: none;text-align:center;letter-spacing:0;background-color: #c7dbff;}

    table.infoTable th {
        background-color: #E1ECFF;
        height: 22px;
    }
  </style>
  <table class="infoTable">
    <tr height="0px">
      <td width="20%"></td>
      <td width="30%"></td>
      <td width="20%"></td>
      <td></td>
    </tr>
    <tr>
      <td class="title">大类：</td>
      <td colspan="3">${certScore.examSubject.name}</td>
    </tr>
    <tr>
      <td class="title">科目/子类：</td>
      <td colspan="3">${certScore.certType.name}</td>
    </tr>
    <tr>
      <td class="title">级别：</td>
      <td>${(certScore.certLevel.name)!"全部/任何"}</td>
      <td class="title">省份：</td>
      <td>${(certScore.division.code[0..1] + "-" + certScore.division.name)!"全国/任何"}</td>
    </tr>
    <tr>
      <td class="title">报考时间：</td>
      <td>${(certScore.examTime.name)!"全部/任何"}</td>
      <td class="title">有效使用日期：</td>
      <td>${certScore.beginOn?string("yyyy-MM-dd")}${("－" + certScore.endOn?string("yyyy-MM-dd"))!"至今"}</td>
    </tr>
    <tr>
      <td class="title" style="vertical-align: top; line-height: 17pt">可替课程：</td>
      <td colspan="3">
        <table class="gridtable">
          <thead class="gridhead">
            <th width="120px">课程代码</th>
            <th>课程名称</th>
            <th width="50px">学分</th>
            <th width="120px">本校得分（显示）</th>
          </thead>
          <tbody>
            [#list certScore.courses as csCourse]
            <tr class="${(csCourse_index % 2 == 0)?string("griddata-even", "griddata-odd")}">
              <td>${csCourse.course.code}</td>
              <td>${csCourse.course.name}</td>
              <td>${csCourse.course.credits}</td>
              <td>${csCourse.score?string("#.####")}（${csCourse.scoreValue}）</td>
            </tr>
            [/#list]
          </tbody>
        </table>
      </td>
    </tr>
    <tr>
      <td class="title">配置更新时间：</td>
      <td>${certScore.updatedAt?string("yyyy-MM-dd HH:mm")}</td>
      <td class="title"></td>
      <td></td>
    </tr>
  </table>
[@b.foot /]
