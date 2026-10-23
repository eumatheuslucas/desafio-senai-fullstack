export default function DataTable({ columns, rows, rowKey, caption }) {
  return (
    <div className="overflow-x-auto rounded-xl border border-line">
      <table className="w-full min-w-max text-left text-sm">
        {caption && <caption className="sr-only">{caption}</caption>}
        <thead className="bg-muted-subtle text-muted-fg">
          <tr>
            {columns.map((column) => (
              <th key={column.header} scope="col" className={`px-3 py-2 font-medium ${column.align === 'right' ? 'text-right' : ''}`}>
                {column.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row) => (
            <tr key={rowKey(row)} className="border-t border-line">
              {columns.map((column) => (
                <td key={column.header} className={`px-3 py-2 ${column.align === 'right' ? 'text-right' : ''}`}>
                  {column.render(row)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
