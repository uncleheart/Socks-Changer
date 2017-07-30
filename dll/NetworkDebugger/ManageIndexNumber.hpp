
#include <climits>
#include <map>
#include<cstdlib>

#ifndef _MANAGE_INDEX_NUMBER
#define  _MANAGE_INDEX_NUMBER

//IndexType= uint16_t
template< typename IndexType, typename ElemType> class ManageIndexNumber
{
private:
	std::map<IndexType, ElemType > IndexSum;		//��ʹ��0
#ifdef _WIN64
	int64_t *IndexSign;
#else
	int32_t *IndexSign;
#endif

	void(*pDeleter)(ElemType &);	//ɾ����
	IndexType IndexNum;				//�������������
	char IndexBit;					//����ֵ��bit��
	int Repeat;						//����indexʱ������ԵĴ���
public:
	ManageIndexNumber(int tRepeat = 100)
		:Repeat(tRepeat)
		, pDeleter(nullptr)
	{
		srand((unsigned)time(NULL));	//��ʵ���������ֻҪ������������������о���
		if (typeid(IndexType) == typeid(uint8_t) || typeid(IndexType) == typeid(int8_t))
		{
			IndexNum = UINT8_MAX;
			IndexBit = 8;
		}
		else if (typeid(IndexType) == typeid(uint16_t) || typeid(IndexType) == typeid(int16_t))
		{
			IndexNum = UINT16_MAX;
			IndexBit = 16;
		}
		else if (typeid(IndexType) == typeid(uint32_t) || typeid(IndexType) == typeid(int32_t))
		{
			IndexNum = UINT32_MAX;
			IndexBit = 32;
		}
		else if (typeid(IndexType) == typeid(uint64_t) || typeid(IndexType) == typeid(int64_t))
		{
			IndexNum = UINT64_MAX;
			IndexBit = 64;
		}

#ifdef _WIN64
		IndexSign = new int64_t[IndexNum / 64];
#else
		IndexSign = new int32_t[IndexNum / 32];
#endif

	}
	~ManageIndexNumber()
	{
		Clear();
		delete IndexSign;

	}
	int Size()
	{
		return IndexSum.size();
	}
	//Dbg(
	//	void Achieve()
	//{
	//	for (std::map<IndexType, ElemType > ::iterator i = IndexSum.begin(); i != IndexSum.end(); i++)
	//	{
	//		/*	Func(i->first,i->second.get);*/
	//		cout << "index  " << i->first << "   status:" << i->second->m_remote_socket.is_open() << i->second->m_local_socket.is_open() << endl;
	//	}
	//})

	void Clear()
	{
		if (pDeleter == nullptr)
		{
			IndexSum.clear();
			return;
		}
		else
		{
			for (std::map<IndexType, ElemType > ::iterator i = IndexSum.begin(); i != IndexSum.end();)
			{
				pDeleter(i->second);
				IndexSum.erase(i++);
			}
		}


	}

	void SetDeleter(void(*p)(ElemType &))
	{
		pDeleter = p;

	}
	IndexType  Insert(ElemType &data, IndexType tIndex = 0)
	{
		int index;
		index = AllocIndex(tIndex);
		if (index == 0)		//
		{
			return 0;
		}

		IndexSum.insert(make_pair(index, data));

		return index;
	}

	//find ��Ҫ�����ж��Ƿ����
	ElemType  Find(IndexType index)
	{
		std::map<IndexType, ElemType > ::iterator it;
		it = IndexSum.find(index);
		if (it == IndexSum.end())
		{
			return NULL;   //ע��  ����������Ͳ���ָ��  �����
		}
		return IndexSum[index];

	}

	bool Erase(IndexType index)
	{
		std::map<IndexType, ElemType > ::iterator it = IndexSum.find(index);

		if (it != IndexSum.end())
		{
			IndexSum.erase(it);
			return true;
		}
		return false;
	}
	bool Exist(IndexType index)
	{
		if (index == 0)
		{
			return true;  //����ʹ��0,0�϶�˵�Ǵ���
		}

#ifdef _WIN64
		return  (IndexSign[IndexNum / 64]) && (1 << (IndexNum % 64 - 1));    //�����Ƕ����  ���Բ��ÿ��Ƕ��������
#else
		return  (IndexSign[IndexNum / 32]) && (1 << (IndexNum % 32 - 1));
#endif

	}

protected:
	inline IndexType GetRand()
	{
		if (IndexBit <= 32)
		{
			return rand();
			//return rand() >> (32 - IndexBit);
		}
		else
		{
			return rand() + (rand() << 32);
		}
	}

	//����һ�����ֵ
	IndexType AllocIndex(IndexType tIndex = 0)
	{
		int n;
		IndexType index;
		if (IndexSum.size() >= IndexNum)
		{
			return 0;
		}
		if (!Exist(tIndex))
		{
			return tIndex;
		}

		for (n = 0; n < Repeat; n++)
		{
			index = GetRand();
			if (!Exist(index))
			{
				goto out;
			}
		}
		if (n == Repeat)  //�����һ�ٴ�Ҳû�гɹ�
		{
			int i, j;
#ifdef _WIN64
			for (i = 0; i < IndexNum / 64; i++)
			{
				if (IndexSign[i] != UINT64_MAX)
				{
					for (j = 0; j < 64; j++)
					{
						if ((IndexSign[i] && 1 << j) == 0)
						{
							index = i * 64 + j + 1;
							goto out;
						}
					}
				}
			}
#else
			for (i = 0; i < IndexNum / 32; i++)
			{
				if (IndexSign[i] != UINT32_MAX)
				{
					for (j = 0; j < 32; j++)
					{
						if ((IndexSign[i] && 1 << j) == 0)
						{
							index = i * 32 + j + 1;
							goto out;
						}
					}
				}
			}
#endif

		}
	out:
		return index;
	}
};

#endif

