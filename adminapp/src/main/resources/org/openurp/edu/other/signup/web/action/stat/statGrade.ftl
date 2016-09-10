[#ftl]
[@b.head/]
[@b.grid sortable="false" ]
<table class="gridtable" id="grid936357727">
   <thead class="gridhead" align="center">
        <tr class="darkColumn" align="center">
            <th>学院</th>
            <th>总人数</th>
                [#list subjects as subject]
                <th>${subject.name}通过数</th>
                [/#list]
        </tr>
    </thead>
    <tbody>
        [#list departMap?keys as departid]
             [#if departid_index % 2 == 0]
                [#assign lessonClass="griddata-even"/]
            [#else]
                [#assign lessonClass="griddata-odd"/]
            [/#if]
        <tr class="${lessonClass}" >
            <td>${departMap[departid][0].name}</td>
            <td>${departMap[departid][1]}</td>
                [#list subjects as subject]
            <td>${(gradeMap[departid].getItem(subject).countors[0])!}</td>
                [/#list]
        </tr>
        [/#list]
    </tbody>
</table>
[/@]
[@b.foot/]