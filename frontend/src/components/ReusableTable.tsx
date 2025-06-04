import { useCallback, useEffect, useMemo, useState } from 'react'
import { Button, DatePicker, Input, Select, Space, Table, message } from 'antd'
import type { TableColumnType, TableProps } from 'antd'
import { SearchOutlined } from '@ant-design/icons'
import axiosInstance from '../utils/axiosInstance'
import type { ColumnType } from 'antd/es/table'
import dayjs from 'dayjs'

interface PageInfo {
  size: number
  number: number
  totalElements: number
  totalPages: number
}

interface ApiResponse<T> {
  code: number
  data: {
    content: T[]
    page: PageInfo
  }
}

export interface FilterConfig {
  type: 'text' | 'select' | 'date'
  placeholder?: string
  options?: { label: string; value: any }[]
  by: string[]
  format?: string
  viewFormat?: string
  showTime?: boolean
}

export interface ReusableColumnType<T> extends TableColumnType<T> {
  filter?: FilterConfig
}

interface ReusableTableProps<T> extends TableProps<T> {
  reloadState?: boolean
  apiUrl: string
  rowKey?: string | ((record: T) => string)
  defaultPageSize?: number
  extraParams?: Record<string, any>
  defaultSortBy?: string
  defaultSortDirection?: 'asc' | 'desc'
  visibleColumns?: string[]
  searchBy?: string
}

const ReusableTable = <T extends object>({
  columns,
  apiUrl,
  reloadState = false,
  rowKey = 'id',
  defaultPageSize = 10,
  defaultSortBy = 'createdAt',
  defaultSortDirection = 'asc',
  visibleColumns = [],
  ...rest
}: ReusableTableProps<T>) => {
  const [data, setData] = useState<T[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: defaultPageSize,
    total: 0
  })
  const [sortState, setSortState] = useState({
    sortBy: defaultSortBy,
    sortDirection: defaultSortDirection
  })
  const [filterState, setFilterState] = useState<Record<string, any>>({})

  const [tempFilterState, setTempFilterState] = useState<Record<string, any>>({})

  const fetchData = useCallback(
    async (
      params: {
        current?: number
        pageSize?: number
        sortBy?: string
        sortDirection?: string
        filter?: Record<string, any>
      } = {}
    ) => {
      setLoading(true)
      try {
        const res = await axiosInstance.post<ApiResponse<T>>(apiUrl, {
          page: (params.current ?? 1) - 1,
          size: params.pageSize ?? defaultPageSize,
          sortBy: params.sortBy ?? defaultSortBy,
          sortDirection: params.sortDirection ?? defaultSortDirection,
          ...params.filter
        })

        const pageInfo = res.data.data.page
        setData(res.data.data.content)
        setPagination((prev) => ({
          ...prev,
          current: pageInfo.number + 1,
          total: pageInfo.totalElements
        }))
      } catch (err) {
        console.error(err)
        message.error('Không thể tải dữ liệu')
      } finally {
        setLoading(false)
      }
    },
    [apiUrl, defaultPageSize, defaultSortBy, defaultSortDirection]
  )

  const getColumnSearchProps = <T extends object>(
    column: ReusableColumnType<T>,
    onFilterChange: () => void
  ): Partial<ColumnType<T>> => {
    const { filter, dataIndex } = column

    if (!filter || !dataIndex) return {}

    return {
      filterDropdown: ({ confirm }) => {
        const handleChange = (key: string, value: any) => {
          setTempFilterState({ ...tempFilterState, [key]: value })
        }

        const handleSearch = () => {
          onFilterChange()
          confirm()
        }

        const handleReset = (keys: string[]) => {
          const newFilterState = { ...tempFilterState }

          keys.forEach((key) => {
            newFilterState[key] = undefined
          })
          setTempFilterState(newFilterState)
          setFilterState({
            ...filterState,
            ...newFilterState
          })
          confirm()
        }

        return (
          <div style={{ padding: 8 }}>
            {filter.type === 'text' &&
              filter.by.map((field) => (
                <Input
                  key={field}
                  placeholder={filter.placeholder}
                  value={tempFilterState[field]}
                  onChange={(e) => handleChange(field, e.target.value)}
                  onPressEnter={handleSearch}
                  style={{ width: 188, marginBottom: 8, display: 'block' }}
                />
              ))}

            {filter.type === 'select' &&
              filter.by.map((field) => (
                <Select
                  key={field}
                  style={{ width: 188, marginBottom: 8, display: 'block' }}
                  placeholder={filter.placeholder}
                  options={filter.options}
                  value={tempFilterState[field]}
                  onChange={(value) => handleChange(field, value)}
                />
              ))}

            {filter.type === 'date' &&
              filter.by.map((field) => (
                <DatePicker
                  key={field}
                  format={filter.viewFormat || filter.format || 'YYYY-MM-DD'}
                  showTime={filter.showTime}
                  style={{ width: 188, marginBottom: 8, display: 'block' }}
                  value={
                    tempFilterState[field]
                      ? dayjs(tempFilterState[field])
                      : tempFilterState[field]
                        ? dayjs(tempFilterState[field])
                        : null
                  }
                  onChange={(date) => handleChange(field, date?.format(filter.format || 'YYYY-MM-DD'))}
                />
              ))}

            <Space>
              <Button
                type='primary'
                onClick={handleSearch}
                icon={<SearchOutlined />}
                size='small'
                style={{ width: 90 }}
              >
                Search
              </Button>
              <Button onClick={() => handleReset(filter.by)} size='small' style={{ width: 90 }}>
                Delete
              </Button>
            </Space>
          </div>
        )
      },
      filterIcon: () => {
        const hasFilter = filter.by.some((field) => !!filterState[field])
        return <SearchOutlined style={{ color: hasFilter ? '#1890ff' : undefined }} />
      }
    }
  }

  const handleTableChange: TableProps<T>['onChange'] = (pagination, _, sorter) => {
    const { field, order } = sorter as any
    const newSortBy = field ?? sortState.sortBy
    const newSortDirection = order === 'ascend' ? 'asc' : order === 'descend' ? 'desc' : defaultSortDirection

    setPagination((prev) => ({
      ...prev,
      current: pagination.current!,
      pageSize: pagination.pageSize!
    }))

    setSortState({ sortBy: newSortBy, sortDirection: newSortDirection })
  }

  const filteredColumns = useMemo(() => {
    return (columns ?? [])
      .filter((column) => visibleColumns.length === 0 || visibleColumns.includes(column.key as string))
      .map((column) => ({
        ...column,
        ...getColumnSearchProps(column, () => {
          setFilterState({
            ...filterState,
            ...tempFilterState
          })
        })
      }))
  }, [columns, visibleColumns, filterState, tempFilterState])

  

  useEffect(() => {
    fetchData({
      current: pagination.current,
      pageSize: pagination.pageSize,
      sortBy: sortState.sortBy,
      sortDirection: sortState.sortDirection,
      filter: filterState
    })
  }, [pagination.current, pagination.pageSize, sortState, filterState, apiUrl, reloadState])

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
  )
}

export default ReusableTable
