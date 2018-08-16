[#ftl]
[@b.head/]
[@b.grid items=signupStats sortable="false" var="signupStat"]
<table class="gridtable" width="100%"  align="center">
   <thead  class="gridhead" align="center">
        <tr class="darkColumn" align="center">
            <th>学期 </th>
            <th>实考人数</th>
            <th>及格人数</th>
            <th>及格率</th>
            <th>第一次参加</th>
            <th>考试人数</th>
            <th>及格人数 </th>
            <th>及格率</th>
        </tr>
    </thead>
    [#if SemesterMap?size==0]
        <tr>
            <td colSpan="8">尚未有人参加考试或者尚未登陆成绩...</td>
        </tr>
    [#else]
        [#list SemesterMap?keys as SemesterId]
              [#if SemesterId_index % 2 == 0]
                    [#assign clazzClass="griddata-even"/]
             [#else]
                    [#assign clazzClass="griddata-odd"/]
             [/#if]
        <tr class="${clazzClass}">
            <td>${(SemesterMap[SemesterId]!)[0].schoolYear!}(${(SemesterMap[SemesterId]!)[0].name!})</td>
            <td>${(SemesterMap[SemesterId]!)[1]!}</td>
            <td>${(gradeMap[SemesterId]!)[1]!}</td>
            <td>${((((gradeMap[SemesterId]!)[1])!0)/((SemesterMap[SemesterId]!)[1]!1))*100}%</td>
            <td>${(first[SemesterId]!)[1]!}</td>
            <td>${(firstT[SemesterId]!)[1]!}</td>
            <td>${(TFirst[SemesterId][1])!}</td>
            <td>${(((TFirst[SemesterId][1])!0)/((firstT[SemesterId]!)[1]!1)*100)}%</td>
        </tr>
        [/#list]
    [/#if]
</table>
[/@]
