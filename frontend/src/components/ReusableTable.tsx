import { useEffect, useState } from 'react';
import { Table, message } from 'antd';
import type { TableProps } from 'antd';
import axios from 'axios';

interface PageInfo {
  size: number;
  number: number;
  totalElements: number;
  totalPages: number;
}

interface ApiResponse<T> {
  code: number;
  data: {
    content: T[];
    page: PageInfo;
  };
}

interface ReusableTableProps<T> extends TableProps<T> {
  apiUrl: string;
  rowKey?: string | ((record: T) => string);
  defaultPageSize?: number;
  extraParams?: Record<string, any>;
  defaultSortBy?: string;
  defaultSortDirection?: 'asc' | 'desc';
  visibleColumns?: string[]; // Thêm prop visibleColumns để lọc cột hiển thị
}

const ReusableTable = <T extends object>({
  columns,
  apiUrl,
  rowKey = 'id',
  defaultPageSize = 10,
  extraParams = {},
  defaultSortBy = 'createdAt',
  defaultSortDirection = 'asc',
  visibleColumns = [], // Mặc định không có cột nào bị ẩn
  ...rest
}: ReusableTableProps<T>) => {
  const [data, setData] = useState<T[]>([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: defaultPageSize,
    total: 0,
  });
  const [sortState, setSortState] = useState({
    sortBy: defaultSortBy,
    sortDirection: defaultSortDirection,
  });

  const fetchData = async (params: {
    current?: number;
    pageSize?: number;
    sortBy?: string;
    sortDirection?: string;
  } = {}) => {
    setLoading(true);
    try {
      const res = await axios.get<ApiResponse<T>>(apiUrl, {
        params: {
          page: (params.current ?? 1) - 1,
          size: params.pageSize ?? defaultPageSize,
          sortBy: params.sortBy ?? sortState.sortBy,
          sortDirection: params.sortDirection ?? sortState.sortDirection,
          ...extraParams,
        },
      });

      const pageInfo = res.data.data.page;
      setData(res.data.data.content);
      setPagination({
        current: pageInfo.number + 1,
        pageSize: pageInfo.size,
        total: pageInfo.totalElements,
      });
    } catch (err) {
      console.error(err);
      message.error('Không thể tải dữ liệu');
    } finally {
      setLoading(false);
    }
  };

  const handleTableChange: TableProps<T>['onChange'] = (pagination, _, sorter) => {
    const { field, order } = sorter as any;
    const newSortBy = field ?? sortState.sortBy;
    const newSortDirection = order === 'ascend' ? 'asc' : order === 'descend' ? 'desc' : sortState.sortDirection;

    setSortState({ sortBy: newSortBy, sortDirection: newSortDirection });

    fetchData({
      current: pagination.current,
      pageSize: pagination.pageSize,
      sortBy: newSortBy,
      sortDirection: newSortDirection,
    });
  };

  // Lọc cột cần hiển thị dựa trên prop visibleColumns
  const filteredColumns = (columns ?? []).filter((column) => 
    visibleColumns.length === 0 || visibleColumns.includes(column.key as string)
  );

  useEffect(() => {
    fetchData({ current: 1, pageSize: defaultPageSize });
  }, [apiUrl, JSON.stringify(extraParams)]);

  return (
    <Table<T>
      columns={filteredColumns}
      dataSource={data}
      rowKey={rowKey}
      pagination={pagination}
      loading={loading}
      onChange={handleTableChange}
      {...rest}
    />
  );
};

export default ReusableTable;
